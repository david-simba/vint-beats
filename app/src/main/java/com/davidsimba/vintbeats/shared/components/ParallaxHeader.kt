package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.shared.theme.VintageBgDark

@Composable
fun ParallaxHeader(
    imageUrl: String?,
    parallaxOffset: Float,
    containerHeight: Dp = 420.dp,
    modifier: Modifier = Modifier,
    bottomContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight)
            .clipToBounds()
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight + 100.dp)
                .graphicsLayer { translationY = parallaxOffset * 0.4f }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.0f),
                            0.45f to Color.Black.copy(alpha = 0.1f),
                            1.0f to VintageBgDark
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .graphicsLayer { translationY = parallaxOffset * 0.4f }
        ) {
            bottomContent()
        }
    }
}
