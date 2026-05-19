package com.davidsimba.vintbeats.feature.search.data

import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import com.davidsimba.vintbeats.feature.search.domain.Track
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(): SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> {
        // TODO: Implement new service
        return emptyList()
    }
}
