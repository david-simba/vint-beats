package com.davidsimba.vintbeats.core.data.search

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track

interface SearchRepository {
    suspend fun searchTracks(query: String): List<Track>
    suspend fun searchArtists(query: String): List<Artist>
    suspend fun searchAlbums(query: String): List<Album>
}
