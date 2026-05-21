package com.davidsimba.vintbeats.feature.search.domain

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val albumImageUrl: String?,
    val previewUrl: String?,
    val durationText: String = ""
)