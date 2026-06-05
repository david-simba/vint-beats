package com.davidsimba.vintbeats.feature.home.domain

import com.davidsimba.vintbeats.core.model.Track

data class ArtistRadioItem(
    val artistId: String,
    val artistName: String,
    val artistImages: List<String>,
    val tracks: List<Track>,
)
