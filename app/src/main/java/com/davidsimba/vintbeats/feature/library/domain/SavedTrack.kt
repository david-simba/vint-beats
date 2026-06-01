package com.davidsimba.vintbeats.feature.library.domain

data class SavedTrack(
    val id: Int,
    val trackId: String,
    val trackTitle: String,
    val trackArtist: String,
    val trackThumbnailUrl: String?,
    val trackDurationText: String,
    val savedAt: Long,
    val audioFilePath: String?,
    val isFavorite: Boolean = false
)
