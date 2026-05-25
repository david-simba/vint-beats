package com.davidsimba.vintbeats.feature.artist.data

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track

interface ArtistRepository {
    suspend fun getArtistDetail(browseId: String): Triple<Artist, List<Track>, String?>
    suspend fun getArtistSongs(songsBrowseId: String): List<Track>
}
