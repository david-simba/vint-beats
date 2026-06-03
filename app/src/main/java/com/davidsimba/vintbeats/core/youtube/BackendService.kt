package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.ExploreCategory
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.PlaylistDetail
import com.davidsimba.vintbeats.core.model.PlaylistSummary
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.album.data.AlbumDetail
import com.davidsimba.vintbeats.feature.artist.data.ArtistDetail
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.davidsimba.vintbeats.core.model.HomeSection
import com.davidsimba.vintbeats.core.model.HomeSectionPlaylists
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

data class ArtistInput(val id: String, val name: String)

data class CategoryPlaylistsResult(
    val title: String,
    val playlists: List<PlaylistSummary>
)

@Singleton
class BackendService @Inject constructor(
    @Named("backend") private val client: OkHttpClient,
    @Named("backendUrl") private val baseUrl: String
) {
    private val gson = Gson()

    @Volatile private var categoriesCache: List<ExploreCategory>? = null

    fun streamProxyUrl(videoId: String): String = "$baseUrl/stream/$videoId"

    suspend fun warmStream(videoId: String) = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$baseUrl/stream/$videoId/warm").build()
            client.newCall(request).execute().close()
        } catch (_: Exception) { }
    }

    suspend fun searchSongs(query: String, limit: Int = 10): List<Track> =
        getList("/search/songs?q=${query.encode()}&limit=$limit", "results")

    suspend fun searchAlbums(query: String, limit: Int = 10): List<Album> =
        getList("/search/albums?q=${query.encode()}&limit=$limit", "results")

    suspend fun searchArtists(query: String, limit: Int = 10): List<Artist> =
        getList("/search/artists?q=${query.encode()}&limit=$limit", "results")

    suspend fun getArtistDetail(browseId: String): ArtistDetail? = get("/artist/$browseId") { body ->
        val root = JsonParser.parseString(body).asJsonObject
        val artist = gson.fromJson(root.get("artist"), Artist::class.java)
        val trackType = object : TypeToken<List<Track>>() {}.type
        val albumType = object : TypeToken<List<Album>>() {}.type
        val topTracks: List<Track> = gson.fromJson(root.getAsJsonArray("topTracks"), trackType) ?: emptyList()
        val albums: List<Album> = gson.fromJson(root.getAsJsonArray("albums"), albumType) ?: emptyList()
        val songsBrowseId = root.get("songsBrowseId")?.takeIf { !it.isJsonNull }?.asString
        ArtistDetail(artist, topTracks, songsBrowseId, albums)
    }

    suspend fun getArtistSongs(songsBrowseId: String): List<Track> =
        getList("/artist/$songsBrowseId/songs", "tracks")

    suspend fun getAlbumDetail(browseId: String): AlbumDetail? = get("/album/$browseId") { body ->
        gson.fromJson(body, AlbumDetail::class.java)
    }

    suspend fun getQueue(videoId: String): List<Track> =
        getList("/queue/$videoId", "tracks")

    suspend fun getHomeFeedPlaylists(artists: List<ArtistInput>): List<HomeSectionPlaylists> =
        withContext(Dispatchers.IO) {
            try {
                val body = gson.toJson(mapOf("artists" to artists))
                    .toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url("$baseUrl/home/playlists").post(body).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    val root = JsonParser.parseString(response.body?.string()).asJsonObject
                    val type = object : TypeToken<List<HomeSectionPlaylists>>() {}.type
                    gson.fromJson<List<HomeSectionPlaylists>>(root.getAsJsonArray("sections"), type) ?: emptyList()
                }
            } catch (_: Exception) { emptyList() }
        }

    suspend fun getHomeFeed(artists: List<ArtistInput>): List<HomeSection> =
        withContext(Dispatchers.IO) {
            try {
                val body = gson.toJson(mapOf("artists" to artists))
                    .toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url("$baseUrl/home").post(body).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    val root = JsonParser.parseString(response.body?.string()).asJsonObject
                    val type = object : TypeToken<List<HomeSection>>() {}.type
                    gson.fromJson<List<HomeSection>>(root.getAsJsonArray("sections"), type) ?: emptyList()
                }
            } catch (_: Exception) { emptyList() }
        }

    suspend fun getLyrics(title: String, artist: String): List<LyricLine> =
        getList("/lyrics?title=${title.encode()}&artist=${artist.encode()}", "lines")

    suspend fun getExploreCategories(): List<ExploreCategory> {
        categoriesCache?.let { return it }
        return getList<ExploreCategory>("/explore", "categories").also { if (it.isNotEmpty()) categoriesCache = it }
    }

    suspend fun getCategoryPlaylists(categoryId: String): CategoryPlaylistsResult? =
        get("/explore/${categoryId.encode()}") { body ->
            gson.fromJson(body, CategoryPlaylistsResult::class.java)
        }

    suspend fun getPlaylistDetail(playlistId: String): PlaylistDetail? =
        get("/playlist/${playlistId.encode()}") { body ->
            gson.fromJson(body, PlaylistDetail::class.java)
        }

    private suspend fun <T> get(path: String, parse: (String) -> T?): T? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$baseUrl$path").build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                parse(body)
            }
        } catch (_: Exception) { null }
    }

    private suspend inline fun <reified T> getList(path: String, arrayKey: String): List<T> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url("$baseUrl$path").build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    val root = JsonParser.parseString(response.body?.string()).asJsonObject
                    val type = object : TypeToken<List<T>>() {}.type
                    gson.fromJson<List<T>>(root.getAsJsonArray(arrayKey), type) ?: emptyList()
                }
            } catch (_: Exception) { emptyList() }
        }

    private fun String.encode(): String = URLEncoder.encode(this, "UTF-8")
}
