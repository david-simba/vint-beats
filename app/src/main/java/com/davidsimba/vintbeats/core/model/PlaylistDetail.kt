package com.davidsimba.vintbeats.core.model

data class PlaylistDetail(
    val title: String,
    val thumbnailUrl: String?,
    val tracks: List<Track>
)
