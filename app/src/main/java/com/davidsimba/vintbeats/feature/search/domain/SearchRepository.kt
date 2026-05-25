package com.davidsimba.vintbeats.feature.search.domain

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
    suspend fun searchArtists(query: String): List<Artist>
}
