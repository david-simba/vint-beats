package com.davidsimba.vintbeats.feature.library.domain.artist

data class SavedArtist(
    val id: Int,
    val artistId: String,
    val name: String,
    val thumbnailUrl: String?,
    val savedAt: Long
)
