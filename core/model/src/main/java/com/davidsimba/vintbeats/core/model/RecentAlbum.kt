package com.davidsimba.vintbeats.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class RecentAlbum(
    val albumId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
)
