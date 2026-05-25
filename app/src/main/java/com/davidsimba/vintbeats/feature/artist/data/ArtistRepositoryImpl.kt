package com.davidsimba.vintbeats.feature.artist.data

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.youtube.YouTubeArtistService
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val artistService: YouTubeArtistService
) : ArtistRepository {
    override suspend fun getArtistDetail(browseId: String): Pair<Artist, List<Track>> =
        artistService.getArtistDetail(browseId) ?: throw Exception("Could not load artist")
}
