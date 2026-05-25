package com.davidsimba.vintbeats.feature.artist.data

import com.davidsimba.vintbeats.core.model.Track

interface ArtistRepository {
    suspend fun getArtistDetail(browseId: String): ArtistDetail
    suspend fun getArtistSongs(songsBrowseId: String): List<Track>
}
