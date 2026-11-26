package com.davidsimba.vintbeats.feature.home.domain

import androidx.compose.runtime.Immutable
import com.davidsimba.vintbeats.core.model.Track

@Immutable
data class ArtistRadioItem(
    val artistId: String,
    val artistName: String,
    val artistImages: List<String>,
    val tracks: List<Track>,
)
