package com.davidsimba.vintbeats.core.data.search

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.network.YouTubeArtistService
import com.davidsimba.vintbeats.core.network.YouTubeSearchService
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchService: YouTubeSearchService,
    private val artistService: YouTubeArtistService
) : SearchRepository {
    override suspend fun searchTracks(query: String): List<Track> =
        searchService.searchSongs(query)

    override suspend fun searchArtists(query: String): List<Artist> =
        artistService.searchArtists(query)

    override suspend fun searchAlbums(query: String): List<Album> =
        searchService.searchAlbums(query)
}
