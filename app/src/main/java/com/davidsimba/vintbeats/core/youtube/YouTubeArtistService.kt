package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named

class YouTubeArtistService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    companion object {
        private const val ARTISTS_FILTER = "EgWKAQIgAWoKEAkQBRAKEAMQBA=="
    }

    suspend fun searchArtists(query: String): List<Artist> =
        withContext(Dispatchers.IO) {
            try {
                val body = """
                    {
                      $YT_CLIENT_CONTEXT,
                      "query": "${query.escapeJson()}",
                      "params": "$ARTISTS_FILTER"
                    }
                """.trimIndent().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("$YT_MUSIC_BASE_URL/search?prettyPrint=false")
                    .post(body)
                    .headers(buildWebHeaders("https://music.youtube.com/search?q=${query.replace(" ", "+")}"))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    parseArtistSearchResponse(response.body?.string() ?: return@withContext emptyList())
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun getArtistDetail(browseId: String): Pair<Artist, List<Track>>? =
        withContext(Dispatchers.IO) {
            try {
                val body = """
                    {
                      $YT_CLIENT_CONTEXT,
                      "browseId": "${browseId.escapeJson()}"
                    }
                """.trimIndent().toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("$YT_MUSIC_BASE_URL/browse?prettyPrint=false")
                    .post(body)
                    .headers(buildWebHeaders("https://music.youtube.com/channel/$browseId"))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    parseArtistBrowseResponse(
                        browseId,
                        response.body?.string() ?: return@withContext null
                    )
                }
            } catch (e: Exception) {
                null
            }
        }

    private fun parseArtistSearchResponse(json: String): List<Artist> {
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
                    parseArtist(renderer)?.let { add(it) }
                }
            }
        }
    }

    private fun parseArtist(r: JsonObject): Artist? {
        val cols = r.arr("flexColumns") ?: return null

        val firstRun = cols.idx(0)?.asJsonObject
            ?.obj("musicResponsiveListItemFlexColumnRenderer")
            ?.obj("text")?.arr("runs")
            ?.idx(0)?.asJsonObject ?: return null

        val name = firstRun.str("text") ?: return null

        val browseId = firstRun.obj("navigationEndpoint")?.obj("browseEndpoint")?.str("browseId")
            ?: r.obj("navigationEndpoint")?.obj("browseEndpoint")?.str("browseId")
            ?: return null

        val secondColRuns = cols.idx(1)?.asJsonObject
            ?.obj("musicResponsiveListItemFlexColumnRenderer")
            ?.obj("text")?.arr("runs")

        val subtitle = secondColRuns?.mapNotNull { it.asJsonObject.str("text") }
            ?.joinToString("")
            ?.takeIf { it.isNotBlank() }

        val thumbnailUrl = r.obj("thumbnail")?.obj("musicThumbnailRenderer")
            ?.obj("thumbnail")?.arr("thumbnails")
            ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()

        return Artist(id = browseId, name = name, thumbnailUrl = thumbnailUrl, subtitle = subtitle)
    }

    private fun parseArtistBrowseResponse(browseId: String, json: String): Pair<Artist, List<Track>>? {
        val root = runCatching { JsonParser.parseString(json).asJsonObject }.getOrNull()
            ?: return null

        val header = root.obj("header")
        val immersive = header?.obj("musicImmersiveHeaderRenderer")
        val visual = header?.obj("musicVisualHeaderRenderer")
        val headerRenderer = immersive ?: visual ?: return null

        val name = headerRenderer.obj("title")?.arr("runs")
            ?.idx(0)?.asJsonObject?.str("text") ?: return null

        val thumbnailUrl = headerRenderer.obj("thumbnail")?.obj("musicThumbnailRenderer")
            ?.obj("thumbnail")?.arr("thumbnails")
            ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()
            ?: headerRenderer.obj("foregroundThumbnail")?.obj("musicThumbnailRenderer")
                ?.obj("thumbnail")?.arr("thumbnails")
                ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()

        val artist = Artist(id = browseId, name = name, thumbnailUrl = thumbnailUrl, subtitle = null)

        val sections = root.obj("contents")
            ?.obj("singleColumnBrowseResultsRenderer")?.arr("tabs")
            ?.idx(0)?.asJsonObject
            ?.obj("tabRenderer")?.obj("content")
            ?.obj("sectionListRenderer")?.arr("contents")
            ?: return artist to emptyList()

        val tracks = buildList {
            for (section in sections) {
                val sectionObj = section.asJsonObject
                val shelf = sectionObj.obj("musicShelfRenderer")
                    ?: sectionObj.obj("musicCarouselShelfRenderer")
                    ?: continue

                val shelfTitle = shelf.obj("title")?.arr("runs")
                    ?.idx(0)?.asJsonObject?.str("text") ?: ""
                if (!shelfTitle.equals("Songs", ignoreCase = true)) continue

                val items = shelf.arr("contents") ?: continue
                for (item in items) {
                    val renderer = item.asJsonObject.obj("musicResponsiveListItemRenderer") ?: continue
                    parseTrackFromBrowse(renderer)?.let { add(it) }
                }
                break
            }
        }

        return artist to tracks
    }

    private fun parseTrackFromBrowse(r: JsonObject): Track? {
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

        val artistName = secondColRuns?.idx(0)?.asJsonObject?.str("text") ?: ""
        val duration = secondColRuns?.last()?.asJsonObject?.str("text") ?: ""

        val thumbnailUrl = r.obj("thumbnail")?.obj("musicThumbnailRenderer")
            ?.obj("thumbnail")?.arr("thumbnails")
            ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()

        return Track(
            id = videoId,
            title = title,
            artist = artistName,
            albumImageUrl = thumbnailUrl,
            previewUrl = null,
            durationText = duration
        )
    }
}
