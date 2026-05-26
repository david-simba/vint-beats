package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.album.data.AlbumDetail
import com.google.gson.JsonArray
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

class YouTubeAlbumService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    suspend fun getAlbumDetail(browseId: String): AlbumDetail? =
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
                    .headers(buildWebHeaders("https://music.youtube.com/browse/$browseId"))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    parseAlbumBrowseResponse(
                        browseId,
                        response.body?.string() ?: return@withContext null
                    )
                }
            } catch (e: Exception) {
                null
            }
        }

    private fun parseAlbumBrowseResponse(browseId: String, json: String): AlbumDetail? {
        val root = runCatching { JsonParser.parseString(json).asJsonObject }.getOrNull()
            ?: return null

        val contents = root.obj("contents")
        val twoCol = contents?.obj("twoColumnBrowseResultsRenderer")

        // Primary: twoColumnBrowseResultsRenderer uses musicResponsiveHeaderRenderer in tabs[0]
        val responsiveHeader = twoCol?.arr("tabs")
            ?.idx(0)?.asJsonObject?.obj("tabRenderer")?.obj("content")
            ?.obj("sectionListRenderer")?.arr("contents")
            ?.idx(0)?.asJsonObject?.obj("musicResponsiveHeaderRenderer")

        // Fallback: legacy header types
        val header = root.obj("header")
        val detailHeader = header?.obj("musicDetailHeaderRenderer")
        val immersiveHeader = header?.obj("musicImmersiveHeaderRenderer")

        val title: String
        var artist = ""
        var year: String? = null
        val thumbnailUrl: String?

        if (responsiveHeader != null) {
            title = responsiveHeader.obj("title")?.arr("runs")
                ?.idx(0)?.asJsonObject?.str("text") ?: return null
            artist = responsiveHeader.obj("straplineTextOne")?.arr("runs")
                ?.idx(0)?.asJsonObject?.str("text") ?: ""
            responsiveHeader.obj("subtitle")?.arr("runs")?.forEach { element ->
                val text = element.asJsonObject.str("text") ?: return@forEach
                if (text.matches(Regex("\\d{4}"))) year = text
            }
            thumbnailUrl = responsiveHeader.obj("thumbnail")
                ?.obj("musicThumbnailRenderer")?.obj("thumbnail")?.arr("thumbnails")
                ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()
        } else {
            val activeHeader = detailHeader ?: immersiveHeader ?: return null
            title = activeHeader.obj("title")?.arr("runs")
                ?.idx(0)?.asJsonObject?.str("text") ?: return null
            val subtitleRuns = detailHeader?.obj("subtitle")?.arr("runs")
                ?: immersiveHeader?.obj("description")?.arr("runs")
            subtitleRuns?.forEach { element ->
                val runObj = element.asJsonObject
                val text = runObj.str("text") ?: return@forEach
                when {
                    text.matches(Regex("\\d{4}")) -> year = text
                    runObj.obj("navigationEndpoint")?.obj("browseEndpoint") != null -> artist = text
                }
            }
            thumbnailUrl = detailHeader?.obj("thumbnail")
                ?.obj("croppedSquareThumbnailRenderer")?.obj("thumbnail")?.arr("thumbnails")
                ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()
                ?: activeHeader.obj("thumbnail")?.obj("musicThumbnailRenderer")
                    ?.obj("thumbnail")?.arr("thumbnails")
                    ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()
        }

        val tracks = parseAlbumTracks(root, artist, thumbnailUrl)

        return AlbumDetail(
            id = browseId,
            title = title,
            artist = artist,
            year = year,
            thumbnailUrl = thumbnailUrl,
            tracks = tracks
        )
    }

    private fun parseAlbumTracks(root: JsonObject, fallbackArtist: String, fallbackThumbnail: String?): List<Track> {
        val contents = root.obj("contents")

        // twoColumnBrowseResultsRenderer: tracks are in secondaryContents, not tabs[0]
        val twoColItems = contents
            ?.obj("twoColumnBrowseResultsRenderer")?.obj("secondaryContents")
            ?.obj("sectionListRenderer")?.arr("contents")
            ?.idx(0)?.asJsonObject?.obj("musicShelfRenderer")?.arr("contents")

        // singleColumnBrowseResultsRenderer fallback
        val singleColItems = contents
            ?.obj("singleColumnBrowseResultsRenderer")?.arr("tabs")
            ?.idx(0)?.asJsonObject?.obj("tabRenderer")?.obj("content")
            ?.obj("sectionListRenderer")?.arr("contents")
            ?.idx(0)?.asJsonObject?.obj("musicShelfRenderer")?.arr("contents")

        val items: JsonArray = twoColItems ?: singleColItems ?: return emptyList()

        return buildList {
            for (item in items) {
                val r = item.asJsonObject.obj("musicResponsiveListItemRenderer") ?: continue
                parseAlbumTrack(r, fallbackArtist, fallbackThumbnail)?.let { add(it) }
            }
        }
    }

    private fun parseAlbumTrack(r: JsonObject, fallbackArtist: String, fallbackThumbnail: String?): Track? {
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
        val artistName = secondColRuns
            ?.mapNotNull { it.asJsonObject.str("text") }
            ?.joinToString("")
            ?.ifBlank { fallbackArtist }
            ?: fallbackArtist

        val duration = r.arr("fixedColumns")
            ?.idx(0)?.asJsonObject
            ?.obj("musicResponsiveListItemFixedColumnRenderer")
            ?.obj("text")?.arr("runs")
            ?.idx(0)?.asJsonObject?.str("text") ?: ""

        val thumbnailUrl = r.obj("thumbnail")?.obj("musicThumbnailRenderer")
            ?.obj("thumbnail")?.arr("thumbnails")
            ?.last()?.asJsonObject?.str("url")?.upscaleThumbnail()
            ?: fallbackThumbnail

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
