package com.davidsimba.vintbeats.shared.components.cassette

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CassetteView(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    rotationDegrees: Float = 0f,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reel")
    val reelAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reelAngle"
    )

    val currentAngle = if (isPlaying) reelAngle else 0f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .graphicsLayer {
                rotationZ = rotationDegrees
            }
    ) {
        val w = size.width
        val h = size.height

        drawCassetteBody(w, h)
        drawLabel(w, h)
        drawStripes(w, h)
        drawTapeWindow(w, h)
        drawReel(centerX = w * 0.33f, centerY = h * 0.48f, radius = h * 0.1f, angle = currentAngle)
        drawReel(centerX = w * 0.67f, centerY = h * 0.48f, radius = h * 0.1f, angle = currentAngle)
        drawBottomDetail(w, h)
        drawScrews(w, h)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F3EC)
@Composable
fun CassetteViewPreview() {
    CassetteView(
        isPlaying = true,
        modifier = Modifier.padding(16.dp)
    )
}