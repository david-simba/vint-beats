package com.davidsimba.vintbeats.core.youtube

import android.util.Log
import com.davidsimba.vintbeats.feature.search.domain.Track
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named

class YouTubeMusicService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    companion object {
        private const val TAG = "YTMusicService"
        private const val BASE_URL = "https://music.youtube.com/youtubei/v1"
        private const val SONGS_FILTER = "EgWKAQIIAWoKEAkQBRAKEAMQBA=="
        private val CLIENT_CONTEXT = """
            "context": {
              "client": {
                "clientName": "WEB_REMIX",
                "clientVersion": "1.20240501.01.00",
                "hl": "en",
                "gl": "US"
              }
            }
        """.trimIndent()
    }

    suspend fun searchSongs(query: String): List<Track> = withContext(Dispatchers.IO) {
        try {
            val body = """
                {
                  $CLIENT_CONTEXT,
                  "query": "${query.escapeJson()}",
                  "params": "$SONGS_FILTER"
                }
            """.trimIndent().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$BASE_URL/search?prettyPrint=false")
                .post(body)
                .headers(buildHeaders("https://music.youtube.com/search?q=${query.replace(" ", "+")}"))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                parseSearchResponse(response.body?.string() ?: return@withContext emptyList())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Returns audio-only stream URL using NewPipeExtractor (handles nsig cipher, client selection).
    suspend fun getAudioStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            val ytUrl = "https://www.youtube.com/watch?v=$videoId"
            val streamInfo = org.schabi.newpipe.extractor.stream.StreamInfo.getInfo(
                org.schabi.newpipe.extractor.NewPipe.getService(0),
                ytUrl
            )
            val best = streamInfo.audioStreams
                .filter { !it.content.isNullOrEmpty() }
                .maxByOrNull { it.averageBitrate }

            if (best == null) {
                Log.e(TAG, "[$videoId] NewPipe → no audio streams available")
                return@withContext null
            }
            Log.d(TAG, "[$videoId] NewPipe → ${streamInfo.audioStreams.size} streams, picked ${best.averageBitrate}bps")
            best.content
        } catch (e: Exception) {
            Log.e(TAG, "[$videoId] NewPipe → ${e::class.simpleName}: ${e.message}")
            null
        }
    }

    // Headers for WEB_REMIX client (search)
    private fun buildHeaders(referer: String) = Headers.Builder()
        .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
        .add("Accept", "*/*")
        .add("Accept-Language", "en-US,en;q=0.5")
        .add("Content-Type", "application/json")
        .add("Origin", "https://music.youtube.com")
        .add("Referer", referer)
        .add("X-YouTube-Client-Name", "67")
        .add("X-YouTube-Client-Version", "1.20240501.01.00")
        .build()

    // --- Search parsing ---

    private fun parseSearchResponse(json: String): List<Track> {
        val root = runCatching { JsonParser.parseString(json).asJsonObject }.getOrNull()
            ?: return emptyList()

        val tabs = root
            .obj("contents")?.obj("tabbedSearchResultsRenderer")?.arr("tabs")
            ?: return emptyList()

        val sections = tabs.idx(0)?.asJsonObject
            ?.obj("tabRenderer")?.obj("content")
            ?.obj("sectionListRenderer")?.arr("contents")
            ?: return emptyList()

        return buildList {
            for (section in sections) {
                val items = section.asJsonObject.obj("musicShelfRenderer")?.arr("contents") ?: continue
                for (item in items) {
                    val renderer = item.asJsonObject.obj("musicResponsiveListItemRenderer") ?: continue
                    parseTrack(renderer)?.let { add(it) }
                }
            }
        }
    }

    private fun parseTrack(r: JsonObject): Track? {
        val cols = r.arr("flexColumns") ?: return null

        val firstRun = cols.idx(0)?.asJsonObject
            ?.obj("musicResponsiveListItemFlexColumnRenderer")
            ?.obj("text")?.arr("runs")
            ?.idx(0)?.asJsonObject ?: return null

        val title = firstRun.str("text") ?: return null

        val videoId = firstRun.obj("navigationEndpoint")?.obj("watchEndpoint")?.str("videoId")
            ?: r.obj("overlay")?.obj("musicItemThumbnailOverlayRenderer")
                ?.obj("content")?.obj("musicPlayButtonRenderer")
                ?.obj("playNavigationEndpoint")?.obj("watchEndpoint")?.str("videoId")
            ?: return null

        val secondColRuns = cols.idx(1)?.asJsonObject
            ?.obj("musicResponsiveListItemFlexColumnRenderer")
            ?.obj("text")?.arr("runs")

        val artist = secondColRuns?.idx(0)?.asJsonObject?.str("text") ?: ""
        val duration = secondColRuns?.last()?.asJsonObject?.str("text") ?: ""

        val thumbnailUrl = r.obj("thumbnail")?.obj("musicThumbnailRenderer")
            ?.obj("thumbnail")?.arr("thumbnails")
            ?.last()?.asJsonObject?.str("url")

        return Track(
            id = videoId,
            title = title,
            artist = artist,
            albumImageUrl = thumbnailUrl,
            previewUrl = null,
            durationText = duration
        )
    }


    // Safe JSON navigation helpers
    private fun JsonObject.obj(key: String): JsonObject? =
        get(key)?.takeIf { it.isJsonObject }?.asJsonObject

    private fun JsonObject.arr(key: String): JsonArray? =
        get(key)?.takeIf { it.isJsonArray }?.asJsonArray

    private fun JsonObject.str(key: String): String? =
        get(key)?.takeIf { it.isJsonPrimitive }?.asString

    private fun JsonArray.idx(i: Int) = if (i < size()) get(i) else null
    private fun JsonArray.last() = if (size() > 0) get(size() - 1) else null

    private fun String.escapeJson() = replace("\\", "\\\\").replace("\"", "\\\"")
}
