package com.davidsimba.vintbeats.core.model

import com.davidsimba.vintbeats.core.model.Track

data class AlbumDetail(
    val id: String,
    val title: String,
    val artist: String,
    val year: String?,
    val thumbnailUrl: String?,
    val tracks: List<Track>
)
