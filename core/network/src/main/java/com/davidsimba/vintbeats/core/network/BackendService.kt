package com.davidsimba.vintbeats.core.network

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.model.AlbumDetail
import com.davidsimba.vintbeats.core.model.ArtistDetail
import com.davidsimba.vintbeats.core.model.ArtistMix
import com.davidsimba.vintbeats.core.model.ArtistRadio
import com.davidsimba.vintbeats.core.model.DjItem
import com.davidsimba.vintbeats.core.model.DjSession
import com.davidsimba.vintbeats.core.model.HomeSectionPlaylists
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.PlaylistDetail
import com.davidsimba.vintbeats.core.model.ExploreCategory
import com.davidsimba.vintbeats.core.model.PlaylistSummary
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

data class ArtistInput(val id: String, val name: String, val thumbnailUrl: String? = null)

data class CategoryPlaylistsResult(
    val title: String,
    val playlists: List<PlaylistSummary>
)

data class DjRecent(val artist: String, val title: String)

sealed interface DjResult {
    data class Success(val session: DjSession) : DjResult
    data object RateLimited : DjResult
    data object NotFound : DjResult
    data object Failure : DjResult
}

@Singleton
class BackendService @Inject constructor(
    @Named("backend") private val client: OkHttpClient,
    @Named("backendUrl") private val baseUrl: String
) {
    private val gson = Gson()

    // La IA con modelos gratuitos puede tardar bastante más que el resto de endpoints.
    private val djClient: OkHttpClient by lazy { client.newBuilder().readTimeout(45, TimeUnit.SECONDS).build() }

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

        val mix: ArtistMix? = root.get("mix")?.takeIf { !it.isJsonNull }?.asJsonObject?.let { m ->
            val mixTrackType = object : TypeToken<List<Track>>() {}.type
            ArtistMix(
                title = m.get("title")?.asString ?: "",
                thumbnailUrl = m.get("thumbnailUrl")?.takeIf { !it.isJsonNull }?.asString,
                tracks = gson.fromJson(m.getAsJsonArray("tracks"), mixTrackType) ?: emptyList(),
                playlistId = m.get("playlistId")?.takeIf { !it.isJsonNull }?.asString,
            )
        }

        val radio: ArtistRadio? = root.get("radio")?.takeIf { !it.isJsonNull }?.asJsonObject?.let { r ->
            val imageType = object : TypeToken<List<String>>() {}.type
            val radioTrackType = object : TypeToken<List<Track>>() {}.type
            ArtistRadio(
                artistImages = gson.fromJson(r.getAsJsonArray("artistImages"), imageType) ?: emptyList(),
                tracks = gson.fromJson(r.getAsJsonArray("tracks"), radioTrackType) ?: emptyList()
            )
        }

        ArtistDetail(artist, topTracks, songsBrowseId, albums, mix, radio)
    }

    suspend fun getArtistSongs(songsBrowseId: String): List<Track> =
        getList("/artist/$songsBrowseId/songs", "tracks")

    suspend fun getAlbumDetail(browseId: String): AlbumDetail? = get("/album/$browseId") { body ->
        gson.fromJson(body, AlbumDetail::class.java)
    }

    suspend fun getQueue(videoId: String): List<Track> =
        getList("/queue/$videoId", "tracks")

    suspend fun getQuickMix(artists: List<ArtistInput>): List<Track> =
        withContext(Dispatchers.IO) {
            try {
                val body = gson.toJson(mapOf("artists" to artists))
                    .toRequestBody("application/json".toMediaType())
                val request = Request.Builder().url("$baseUrl/home/quick-mix").post(body).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyList()
                    val root = JsonParser.parseString(response.body?.string()).asJsonObject
                    val type = object : TypeToken<List<Track>>() {}.type
                    gson.fromJson<List<Track>>(root.getAsJsonArray("tracks"), type) ?: emptyList()
                }
            } catch (_: Exception) { emptyList() }
        }

    suspend fun getHomeMix(artistId: String, artistName: String): PlaylistDetail? =
        get("/home/mix/${artistId.encode()}?name=${artistName.encode()}") { body ->
            gson.fromJson(body, PlaylistDetail::class.java)
        }

    suspend fun getArtistRadio(artistId: String): Pair<List<String>, List<Track>>? =
        get("/home/radio/${artistId.encode()}") { body ->
            val root = JsonParser.parseString(body).asJsonObject
            val imageType = object : TypeToken<List<String>>() {}.type
            val trackType = object : TypeToken<List<Track>>() {}.type
            val images: List<String> = gson.fromJson(root.getAsJsonArray("artistImages"), imageType) ?: emptyList()
            val tracks: List<Track> = gson.fromJson(root.getAsJsonArray("tracks"), trackType) ?: emptyList()
            if (tracks.isEmpty()) null else images to tracks
        }

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

    /** Pide al DJ una tanda de canciones. [next] = true usa `/dj/next` (continuar con lo ya sonado en [recent]). */
    suspend fun djSession(
        prompt: String,
        language: String,
        recent: List<DjRecent> = emptyList(),
        favoriteArtists: List<String> = emptyList(),
        count: Int = 10,
        next: Boolean = false,
    ): DjResult = withContext(Dispatchers.IO) {
        try {
            val body = gson.toJson(
                mapOf(
                    "prompt" to prompt,
                    "count" to count,
                    "language" to language,
                    "recent" to recent,
                    "favoriteArtists" to favoriteArtists,
                )
            ).toRequestBody("application/json".toMediaType())
            val path = if (next) "/dj/next" else "/dj/session"
            val request = Request.Builder().url("$baseUrl$path").post(body).build()
            djClient.newCall(request).execute().use { response ->
                when {
                    response.code == 429 -> return@withContext DjResult.RateLimited
                    response.code == 404 -> return@withContext DjResult.NotFound
                    !response.isSuccessful -> return@withContext DjResult.Failure
                }
                val root = JsonParser.parseString(response.body?.string()).asJsonObject
                val items = root.getAsJsonArray("items").mapNotNull { el ->
                    val track = gson.fromJson(el.asJsonObject.get("track"), Track::class.java)
                        ?: return@mapNotNull null
                    DjItem(track)
                }
                if (items.isEmpty()) return@withContext DjResult.NotFound
                DjResult.Success(
                    DjSession(
                        intro = root.get("intro")?.takeIf { !it.isJsonNull }?.asString.orEmpty(),
                        outro = root.get("outro")?.takeIf { !it.isJsonNull }?.asString.orEmpty(),
                        items = items,
                        fallback = root.get("fallback")?.takeIf { !it.isJsonNull }?.asBoolean ?: false,
                    )
                )
            }
        } catch (_: Exception) { DjResult.Failure }
    }

    private fun String.encode(): String = URLEncoder.encode(this, "UTF-8")
}
