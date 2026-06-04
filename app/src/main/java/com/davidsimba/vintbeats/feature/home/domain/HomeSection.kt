package com.davidsimba.vintbeats.feature.home.domain

data class PlaylistItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String?,
)

data class HomeSectionPlaylists(
    val title: String,
    val playlists: List<PlaylistItem>,
    val isPrimary: Boolean = false,
)
