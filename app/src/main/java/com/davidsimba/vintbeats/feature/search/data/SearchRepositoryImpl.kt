package com.davidsimba.vintbeats.feature.search.data

import com.davidsimba.vintbeats.core.youtube.YouTubeSearchService
import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import com.davidsimba.vintbeats.feature.search.domain.Track
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: YouTubeSearchService
) : SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> =
        searchService.searchSongs(query)
}
