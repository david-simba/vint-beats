package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.feature.player.ui.PlayerState
import com.davidsimba.vintbeats.shared.components.background.Background
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun MiniPlayer(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    thumbnailUrl: String?,
    playerState: PlayerState,
    positionMs: Long = 0L,
    durationMs: Long = 0L,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onTap: () -> Unit
) {
    val isPlaying = playerState is PlayerState.Playing
    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    val swipeThresholdPx = with(LocalDensity.current) { 80.dp.toPx() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var totalDrag = 0f
                    var triggered = false
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            if (triggered) change.consume()
                            break
                        }
                        totalDrag += change.position.x - change.previousPosition.x
                        if (!triggered && totalDrag < -swipeThresholdPx) {
                            triggered = true
                            change.consume()
                            onSkipNext()
                        } else if (triggered) {
                            change.consume()
                        }
                    }
                }
            }
    ) {
        Background(thumbnailUrl = thumbnailUrl, horizontal = true, artColorsOnly = true, modifier = Modifier.matchParentSize())
        Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.45f)))

        Column {
            TrackCard(
                title = title,
                artist = artist,
                thumbnailUrl = thumbnailUrl,
                imageSize = 46.dp,
                onClick = onTap,
                modifier = Modifier.fillMaxWidth(),
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onTogglePlayPause) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = VintageWhitePure,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(onClick = onSkipNext) {
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = "Next",
                                tint = VintageWhitePure,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VintageWhitePure.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(4.dp)
                        .align(Alignment.CenterStart)
                        .background(VintageWhitePure.copy(alpha = 0.85f))
                )
            }
        }
    }
}
