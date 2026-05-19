package com.davidsimba.vintbeats.feature.search.domain

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
}