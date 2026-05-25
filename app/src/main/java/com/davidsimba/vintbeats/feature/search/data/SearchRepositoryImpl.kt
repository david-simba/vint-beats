package com.davidsimba.vintbeats.feature.search.data

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.youtube.YouTubeArtistService
import com.davidsimba.vintbeats.core.youtube.YouTubeSearchService
import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: YouTubeSearchService,
    private val artistService: YouTubeArtistService
) : SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> =
        searchService.searchSongs(query)

    override suspend fun searchArtists(query: String): List<Artist> =
        artistService.searchArtists(query)
}
