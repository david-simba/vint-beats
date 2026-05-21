package com.davidsimba.vintbeats.shared.components.cassette

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight

@Composable
fun CassetteView(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    rotationDegrees: Float = 0f,
    isFloating: Boolean = false,
    cassetteColor: Color = VintageBlackMid,
    lineColor: Color = VintageRedLight,
    drawRainbow: Boolean = false
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

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    val currentAngle = if (isPlaying) reelAngle else 0f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .graphicsLayer {
                rotationZ = rotationDegrees
                translationY = if (isFloating) floatOffset else 0f
            }
    ) {
        val w = size.width
        val h = size.height

        val dx = w * 0.022f
        val dy = h * 0.048f
        val sideColor = Color(
            red = (cassetteColor.red * 0.45f).coerceIn(0f, 1f),
            green = (cassetteColor.green * 0.45f).coerceIn(0f, 1f),
            blue = (cassetteColor.blue * 0.45f).coerceIn(0f, 1f),
        )
        withTransform({ translate(dx, dy) }) { drawCassetteBody(w, h, sideColor.copy(alpha = 0.5f)) }
        withTransform({ translate(dx, dy) })  { drawCassetteBody(w, h, sideColor) }

        drawCassetteBody(w, h, cassetteColor)
        drawLabel(w, h)
        if (drawRainbow) drawStripes(w, h)
        else drawSeparatorLine(w * 0.88f, h * 0.14f, lineColor)
        drawTapeWindow(w, h, cassetteColor)
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
        modifier = Modifier.padding(16.dp),
    )
}