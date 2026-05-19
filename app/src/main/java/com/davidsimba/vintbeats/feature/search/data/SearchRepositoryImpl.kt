package com.davidsimba.vintbeats.feature.search.data

import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import com.davidsimba.vintbeats.feature.search.domain.Track
import retrofit2.HttpException
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: SpotifySearchApi
): SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> {
        try {
            return api.searchTracks(query, type = "track", limit = 10).tracks.items.map { dto ->
                Track(
                    id = dto.id,
                    title = dto.name,
                    artist = dto.artists.firstOrNull()?.name ?: "Unknown",
                    albumImageUrl = dto.album.images.firstOrNull()?.url,
                    previewUrl = dto.previewUrl
                )
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            throw Exception("HTTP ${e.code()}: $errorBody", e)
        }
    }
}