package com.davidsimba.vintbeats.feature.search.data

import com.davidsimba.vintbeats.core.youtube.YouTubeMusicService
import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import com.davidsimba.vintbeats.feature.search.domain.Track
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val youTubeMusic: YouTubeMusicService
) : SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> =
        youTubeMusic.searchSongs(query)
}
