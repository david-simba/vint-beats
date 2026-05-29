package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.data.ArtistDetail
import javax.inject.Inject

class YouTubeArtistService @Inject constructor(
    private val backendService: BackendService
) {
    suspend fun searchArtists(query: String): List<Artist> = backendService.searchArtists(query)
    suspend fun getArtistDetail(browseId: String): ArtistDetail? = backendService.getArtistDetail(browseId)
    suspend fun getArtistSongs(songsBrowseId: String): List<Track> = backendService.getArtistSongs(songsBrowseId)
}
