package com.davidsimba.vintbeats.shared.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val barConfig = listOf(
    500 to 0.4f,
    350 to 1.0f,
    600 to 0.6f,
    280 to 0.9f,
    450 to 0.5f,
    380 to 0.75f,
    520 to 0.55f,
    300 to 0.85f,
    470 to 0.45f,
    420 to 0.95f,
    560 to 0.65f,
    310 to 0.8f,
)

@Composable
fun EqualizerBars(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    maxHeight: Dp = 16.dp,
    spacing: Dp = 2.dp
) {
    val transition = rememberInfiniteTransition(label = "eq")

    val fractions = barConfig.map { (duration, peak) ->
        val fraction by transition.animateFloat(
            initialValue = 0.15f,
            targetValue = if (isPlaying) peak else 0.15f,
            animationSpec = InfiniteRepeatableSpec(
                animation = tween(durationMillis = duration),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar"
        )
        fraction
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        fractions.forEach { fraction ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(fraction)
                    .background(color, RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            )
        }
    }
}
