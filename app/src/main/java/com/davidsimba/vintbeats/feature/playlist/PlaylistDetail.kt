package com.davidsimba.vintbeats.feature.playlist

import com.davidsimba.vintbeats.core.model.Track

data class PlaylistDetail(
    val title: String,
    val thumbnailUrl: String?,
    val tracks: List<Track>,
)
