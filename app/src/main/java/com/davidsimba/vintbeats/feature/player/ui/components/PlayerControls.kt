package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isLoading: Boolean,
    positionMs: Long,
    durationMs: Long,
    accentColor: Color = VintageRedLight,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SeekBar(
                positionMs = positionMs,
                durationMs = durationMs,
                isLoading = isLoading,
                accentColor = accentColor,
                onSeek = onSeek,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isLoading) "--:--" else formatMs(positionMs),
                    color = VintageGrayMid,
                    fontSize = 12.sp
                )
                Text(
                    text = if (isLoading) "--:--" else formatMs(durationMs),
                    color = VintageGrayMid,
                    fontSize = 12.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(modifier = Modifier.size(40.dp), onClick = {}) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = "Shuffle",
                    tint = VintageWhitePure.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(modifier = Modifier.size(48.dp), onClick = {}) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = "Previous",
                    tint = VintageWhitePure,
                    modifier = Modifier.size(32.dp)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .border(1.5.dp, VintageWhitePure.copy(alpha = 0.3f), CircleShape)
            ) {
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = VintageWhitePure,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(modifier = Modifier.size(48.dp), onClick = {}) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Next",
                    tint = VintageWhitePure,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(modifier = Modifier.size(40.dp), onClick = {}) {
                Icon(
                    imageVector = Icons.Rounded.Repeat,
                    contentDescription = "Repeat",
                    tint = VintageWhitePure.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun SeekBar(
    positionMs: Long,
    durationMs: Long,
    isLoading: Boolean,
    accentColor: Color,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }

    val fraction = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val displayFraction = if (isDragging) dragFraction else fraction

    val shimmerTransition = rememberInfiniteTransition(label = "seekbar_shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Canvas(
        modifier = modifier
            .height(20.dp)
            .pointerInput(durationMs, isLoading) {
                if (isLoading) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    isDragging = true
                    dragFraction = (down.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                    down.consume()
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) break
                        dragFraction = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                        change.consume()
                    }
                    isDragging = false
                    onSeek((dragFraction * durationMs).toLong().coerceIn(0L, durationMs))
                }
            }
    ) {
        val w = size.width
        val cy = size.height / 2f
        val trackH = 3.dp.toPx()
        val thumbR = 5.dp.toPx()

        drawRoundRect(
            color = VintageGrayDeep,
            topLeft = Offset(0f, cy - trackH / 2),
            size = Size(w, trackH),
            cornerRadius = CornerRadius(trackH / 2)
        )

        if (isLoading) {
            val segmentW = w * 0.35f
            val segmentStart = w * shimmerOffset
            val drawStart = maxOf(segmentStart, 0f)
            val drawEnd = minOf(segmentStart + segmentW, w)
            val drawWidth = drawEnd - drawStart
            if (drawWidth > 0f) {
                drawRoundRect(
                    color = VintageGrayMid.copy(alpha = 0.55f),
                    topLeft = Offset(drawStart, cy - trackH / 2),
                    size = Size(drawWidth, trackH),
                    cornerRadius = CornerRadius(trackH / 2)
                )
            }
        } else {
            val thumbX = (w * displayFraction).coerceIn(thumbR, w - thumbR)
            if (displayFraction > 0f) {
                drawRoundRect(
                    color = accentColor,
                    topLeft = Offset(0f, cy - trackH / 2),
                    size = Size(thumbX, trackH),
                    cornerRadius = CornerRadius(trackH / 2)
                )
            }
            drawCircle(color = accentColor, radius = thumbR, center = Offset(thumbX, cy))
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
