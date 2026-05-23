package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.core.model.Track
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named

class YouTubeSearchService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    companion object {
        private const val SONGS_FILTER = "EgWKAQIIAWoKEAkQBRAKEAMQBA=="
    }

    suspend fun searchSongs(query: String): List<Track> =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val body = """
                    {
                      $YT_CLIENT_CONTEXT,
                      "query": "${query.escapeJson()}",
                      "params": "$SONGS_FILTER"
                    }
                """.trimIndent().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("$YT_MUSIC_BASE_URL/search?prettyPrint=false")
                    .post(body)
                    .headers(buildWebHeaders("https://music.youtube.com/search?q=${query.replace(" ", "+")}"))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    parseSearchResponse(response.body?.string() ?: return@withContext emptyList())
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

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
            ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()

        return Track(
            id = videoId,
            title = title,
            artist = artist,
            albumImageUrl = thumbnailUrl,
            previewUrl = null,
            durationText = duration
        )
    }
}
