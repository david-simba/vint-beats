package com.davidsimba.vintbeats.feature.cassette.domain

import androidx.compose.ui.graphics.Color

data class SavedCassette(
    val id: Int,
    val trackId: String,
    val trackTitle: String,
    val trackArtist: String,
    val trackThumbnailUrl: String?,
    val trackDurationText: String,
    val cassetteColor: Color,
    val lineColor: Color,
    val isRainbow: Boolean,
    val savedAt: Long
)
