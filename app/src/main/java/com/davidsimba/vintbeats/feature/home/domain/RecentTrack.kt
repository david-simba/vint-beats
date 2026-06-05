package com.davidsimba.vintbeats.feature.home.domain

data class RecentTrack(
    val trackId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
)
