package com.davidsimba.vintbeats.core.network

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Track
import javax.inject.Inject

class YouTubeSearchService @Inject constructor(
    private val backendService: BackendService
) {
    suspend fun searchSongs(query: String): List<Track> = backendService.searchSongs(query)
    suspend fun searchAlbums(query: String): List<Album> = backendService.searchAlbums(query)
}
