package com.davidsimba.vintbeats.feature.artist.data

import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.youtube.YouTubeArtistService
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val artistService: YouTubeArtistService
) : ArtistRepository {
    override suspend fun getArtistDetail(browseId: String): ArtistDetail =
        artistService.getArtistDetail(browseId) ?: throw Exception("Could not load artist")

    override suspend fun getArtistSongs(songsBrowseId: String): List<Track> =
        artistService.getArtistSongs(songsBrowseId)
}
