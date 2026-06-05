package com.davidsimba.vintbeats.feature.home.domain

import androidx.compose.runtime.Immutable

@Immutable
data class RecentTrack(
    val trackId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
)
