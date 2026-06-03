package com.davidsimba.vintbeats.feature.library.domain.album

data class SavedAlbum(
    val id: Int,
    val albumId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val savedAt: Long
)
