package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.core.model.Track
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named

class YouTubeQueueService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    suspend fun getUpNextTracks(videoId: String): List<Track> = withContext(Dispatchers.IO) {
        try {
            val body = """
                {
                  $YT_CLIENT_CONTEXT,
                  "videoId": "$videoId",
                  "playlistId": "RDAMVM$videoId"
                }
            """.trimIndent().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$YT_MUSIC_BASE_URL/next?prettyPrint=false")
                .post(body)
                .headers(buildWebHeaders("https://music.youtube.com/watch?v=$videoId"))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val rawBody = response.body?.string() ?: return@withContext emptyList()
                val json = JsonParser.parseString(rawBody).asJsonObject

                val singleCol = json.obj("contents")?.obj("singleColumnMusicWatchNextResultsRenderer")
                    ?: return@withContext emptyList()

                val queueRenderer = singleCol.obj("tabbedRenderer")
                    ?.obj("watchNextTabbedResultsRenderer")
                    ?.arr("tabs")
                    ?.idx(0)?.asJsonObject
                    ?.obj("tabRenderer")?.obj("content")?.obj("musicQueueRenderer")
                    ?: return@withContext emptyList()

                val content = queueRenderer.obj("content")
                    ?.obj("playlistPanelRenderer")
                    ?.arr("contents") ?: return@withContext emptyList()

                buildList {
                    for (item in content) {
                        val r = item.asJsonObject.obj("playlistPanelVideoRenderer") ?: continue
                        val id = r.str("videoId") ?: continue
                        if (id == videoId) continue
                        val title = r.obj("title")?.arr("runs")?.idx(0)?.asJsonObject?.str("text") ?: continue
                        val artist = r.obj("shortBylineText")?.arr("runs")?.idx(0)?.asJsonObject?.str("text")
                            ?: r.obj("longByLineText")?.arr("runs")?.idx(0)?.asJsonObject?.str("text")
                            ?: ""
                        val duration = r.obj("lengthText")?.arr("runs")?.idx(0)?.asJsonObject?.str("text") ?: ""
                        val thumb = r.obj("thumbnail")?.arr("thumbnails")?.last()?.asJsonObject?.str("url")
                        add(Track(id = id, title = title, artist = artist, albumImageUrl = thumb, previewUrl = null, durationText = duration))
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
