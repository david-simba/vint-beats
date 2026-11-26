package com.davidsimba.vintbeats.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val albumImageUrl: String?,
    val previewUrl: String?,
    val durationText: String = "",
    val artistId: String? = null
)
