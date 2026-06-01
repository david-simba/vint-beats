package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import coil.compose.AsyncImage
import kotlin.math.roundToInt

@Composable
fun PlayerBackground(
    currentImageUrl: String?,
    nextImageUrl: String?,
    previousImageUrl: String?,
    offsetX: Float,
    componentWidth: Float,
    backgroundColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.verticalGradient(
        0f to Color.Black.copy(alpha = 0.1f),
        0.55f to Color.Black.copy(alpha = 0.25f),
        1f to backgroundColor
    )

    Box(modifier = modifier) {
        AsyncImage(
            model = previousImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((-componentWidth + offsetX).roundToInt(), 0) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((-componentWidth + offsetX).roundToInt(), 0) }
                .background(gradient)
        )

        AsyncImage(
            model = nextImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((componentWidth + offsetX).roundToInt(), 0) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((componentWidth + offsetX).roundToInt(), 0) }
                .background(gradient)
        )

        AsyncImage(
            model = currentImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .background(gradient)
        )
    }
}
