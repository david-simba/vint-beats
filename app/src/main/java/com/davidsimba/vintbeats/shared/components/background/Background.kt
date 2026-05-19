package com.davidsimba.vintbeats.shared.components.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.davidsimba.vintbeats.shared.theme.VintageBgAccent
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun Background(modifier: Modifier = Modifier) {
    val grainPoints = remember {
        List(300) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 0.8f + 0.2f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(color = VintageBgBase)

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    VintageBgDark.copy(alpha = 0.7f),
                    Color.Transparent,
                    Color.Transparent,
                    VintageBgDark.copy(alpha = 0.9f),
                ),
                startY = 0f,
                endY = h
            )
        )

        grainPoints.forEach { (rx, ry, alpha) ->
            drawCircle(
                color = VintageBgAccent.copy(alpha = alpha * 0.15f),
                radius = 1.2f,
                center = Offset(rx * w, ry * h)
            )
        }

        for (i in 1..7) {
            drawCircle(
                color = VintageBgAccent.copy(alpha = 0.18f),
                radius = i * 80f,
                center = Offset(w, 0f),
                style = Stroke(width = 1f)
            )
        }

        val spacing = w * 0.055f
        val cols = (w / spacing).toInt() + 1
        val rows = (h / spacing).toInt() + 1
        val waveAmplitude = spacing * 0.4f

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val baseX = col * spacing
                val baseY = row * spacing

                val waveOffset = sin(col * 0.4f + row * 0.3f) * waveAmplitude

                val x = baseX
                val y = baseY + waveOffset

                val distFromBottomLeft = sqrt(
                    (x / w) * (x / w) +
                            ((h - y) / h) * ((h - y) / h)
                )
                val alpha = (1f - distFromBottomLeft * 1.1f).coerceIn(0f, 0.65f)

                if (alpha > 0.02f) {
                    val radius = alpha * 3.5f + 0.8f
                    drawCircle(
                        color = VintageBgAccent.copy(alpha = alpha),
                        radius = radius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}