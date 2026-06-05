package com.davidsimba.vintbeats.feature.home.domain

data class RecentAlbum(
    val albumId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
)
