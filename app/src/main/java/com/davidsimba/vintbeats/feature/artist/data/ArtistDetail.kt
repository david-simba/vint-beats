package com.davidsimba.vintbeats.feature.artist.data

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track

data class ArtistDetail(
    val artist: Artist,
    val topTracks: List<Track>,
    val songsBrowseId: String?,
    val albums: List<Album>
)
