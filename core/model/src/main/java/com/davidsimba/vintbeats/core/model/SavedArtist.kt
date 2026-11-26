package com.davidsimba.vintbeats.core.model

data class SavedArtist(
    val id: Int,
    val artistId: String,
    val name: String,
    val thumbnailUrl: String?,
    val savedAt: Long
)
