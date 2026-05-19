package com.davidsimba.vintbeats.feature.cassette.domain

import androidx.compose.ui.graphics.Color
import com.davidsimba.vintbeats.feature.search.domain.Track

data class CassetteConfig(
    val track: Track,
    val cassetteColor: Color,
    val lineColor: Color,
    val isRainbow: Boolean
)