package com.davidsimba.vintbeats.core.youtube

import android.util.Log
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named

class YouTubeLyricsService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    companion object {
        private const val TAG = "YTLyricsService"
    }

    suspend fun getLyrics(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            val nextBody = """
                {
                  $YT_CLIENT_CONTEXT,
                  "videoId": "$videoId"
                }
            """.trimIndent().toRequestBody("application/json".toMediaType())

            val nextRequest = Request.Builder()
                .url("$YT_MUSIC_BASE_URL/next?prettyPrint=false")
                .post(nextBody)
                .headers(buildWebHeaders("https://music.youtube.com/watch?v=$videoId"))
                .build()

            val browseId = client.newCall(nextRequest).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val json = JsonParser.parseString(response.body?.string() ?: return@withContext null).asJsonObject
                val tabs = json
                    .obj("contents")?.obj("singleColumnMusicWatchNextResultsRenderer")
                    ?.obj("tabbedRenderer")?.obj("watchNextTabbedResultsRenderer")
                    ?.arr("tabs") ?: return@withContext null
                var id: String? = null
                for (tab in tabs) {
                    val renderer = tab.asJsonObject.obj("tabRenderer") ?: continue
                    if (renderer.str("title") == "Lyrics") {
                        id = renderer.obj("endpoint")?.obj("browseEndpoint")?.str("browseId")
                        break
                    }
                }
                id
            } ?: run {
                Log.d(TAG, "[$videoId] No lyrics browseId found")
                return@withContext null
            }

            val browseBody = """
                {
                  $YT_CLIENT_CONTEXT,
                  "browseId": "$browseId"
                }
            """.trimIndent().toRequestBody("application/json".toMediaType())

            val browseRequest = Request.Builder()
                .url("$YT_MUSIC_BASE_URL/browse?prettyPrint=false")
                .post(browseBody)
                .headers(buildWebHeaders("https://music.youtube.com/watch?v=$videoId"))
                .build()

            client.newCall(browseRequest).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val json = JsonParser.parseString(response.body?.string() ?: return@withContext null).asJsonObject
                val runs = json
                    .obj("contents")?.obj("sectionListRenderer")
                    ?.arr("contents")?.idx(0)?.asJsonObject
                    ?.obj("musicDescriptionShelfRenderer")
                    ?.obj("description")?.arr("runs") ?: return@withContext null
                val lyrics = buildString {
                    for (run in runs) append(run.asJsonObject.str("text") ?: "")
                }.trim()
                if (lyrics.isEmpty()) null else lyrics.also { Log.d(TAG, "[$videoId] Lyrics loaded (${it.length} chars)") }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$videoId] Lyrics error: ${e.message}")
            null
        }
    }
}
