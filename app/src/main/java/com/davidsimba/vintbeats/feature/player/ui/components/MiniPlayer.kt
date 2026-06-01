package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.player.ui.PlayerState
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.components.background.Background
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MiniPlayer(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    thumbnailUrl: String?,
    nextTrack: Track?,
    previousTrack: Track?,
    playerState: PlayerState,
    positionMs: Long = 0L,
    durationMs: Long = 0L,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onTap: () -> Unit
) {
    val isPlaying = playerState is PlayerState.Playing
    val isLoading = playerState is PlayerState.Loading
    val progress = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    val shimmerTransition = rememberInfiniteTransition(label = "mini_shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mini_shimmer_offset"
    )

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var componentWidth by remember { mutableFloatStateOf(0f) }
    var textZoneWidth by remember { mutableFloatStateOf(0f) }
    val hasPrevious by rememberUpdatedState(previousTrack != null)

    Box(
        modifier = modifier
            .onSizeChanged { componentWidth = it.width.toFloat() }
            .padding(start = 6.dp, end = 6.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            when {
                                offsetX.value < -(componentWidth * 0.4f) -> {
                                    change.consume()
                                    coroutineScope.launch {
                                        offsetX.animateTo(-componentWidth, tween(150))
                                        onSkipNext()
                                        offsetX.snapTo(0f)
                                    }
                                }
                                offsetX.value > (componentWidth * 0.4f) -> {
                                    change.consume()
                                    coroutineScope.launch {
                                        offsetX.animateTo(componentWidth, tween(150))
                                        onSkipPrevious()
                                        offsetX.snapTo(0f)
                                    }
                                }
                                else -> {
                                    coroutineScope.launch {
                                        offsetX.animateTo(
                                            0f,
                                            spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            )
                                        )
                                    }
                                }
                            }
                            break
                        }
                        val delta = change.position.x - change.previousPosition.x
                        val newOffset = (offsetX.value + delta).coerceIn(-componentWidth, if (hasPrevious) componentWidth else 0f)
                        coroutineScope.launch { offsetX.snapTo(newOffset) }
                        if (kotlin.math.abs(offsetX.value) > viewConfiguration.touchSlop) {
                            change.consume()
                        }
                    }
                }
            }
    ) {
        Background(
            thumbnailUrl = thumbnailUrl,
            horizontal = true,
            artColorsOnly = true,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onTap)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(VintageGrayDeep)
                ) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }

                // Text slides proportionally: scaled so current exits and next enters
                // exactly in sync with the swipe distance, regardless of zone width.
                val scale = if (componentWidth > 0f) textZoneWidth / componentWidth else 1f
                val textOffset = (offsetX.value * scale).roundToInt()
                val overallFraction = if (componentWidth > 0f) (kotlin.math.abs(offsetX.value) / componentWidth).coerceIn(0f, 1f) else 0f
                val nextFraction = if (componentWidth > 0f) (-offsetX.value / componentWidth).coerceIn(0f, 1f) else 0f
                val prevFraction = if (componentWidth > 0f) (offsetX.value / componentWidth).coerceIn(0f, 1f) else 0f

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clipToBounds()
                        .onSizeChanged { textZoneWidth = it.width.toFloat() }
                ) {
                    TrackInfo(
                        title = title,
                        artist = artist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(textOffset, 0) }
                            .alpha(1f - overallFraction)
                    )
                    if (nextTrack != null) {
                        TrackInfo(
                            title = nextTrack.title,
                            artist = nextTrack.artist,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(textZoneWidth.roundToInt() + textOffset, 0) }
                                .alpha(nextFraction)
                        )
                    }
                    if (previousTrack != null) {
                        TrackInfo(
                            title = previousTrack.title,
                            artist = previousTrack.artist,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { IntOffset(-textZoneWidth.roundToInt() + textOffset, 0) }
                                .alpha(prevFraction)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) VintageRedLight else VintageWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = VintageWhite,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(
                        onClick = onSkipNext,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = "Next",
                            tint = VintageWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                val w = size.width
                val h = size.height
                val trackH = 4.dp.toPx()
                val cy = h / 2f

                drawRect(
                    color = VintageWhite.copy(alpha = 0.15f),
                    topLeft = Offset(0f, cy - trackH / 2),
                    size = Size(w, trackH)
                )

                if (isLoading) {
                    val segmentW = w * 0.35f
                    val start = w * shimmerOffset
                    val drawStart = maxOf(start, 0f)
                    val drawEnd = minOf(start + segmentW, w)
                    val drawWidth = drawEnd - drawStart
                    if (drawWidth > 0f) {
                        drawRect(
                            color = VintageWhite.copy(alpha = 0.45f),
                            topLeft = Offset(drawStart, cy - trackH / 2),
                            size = Size(drawWidth, trackH)
                        )
                    }
                } else if (progress > 0f) {
                    drawRect(
                        color = VintageRedLight,
                        topLeft = Offset(0f, cy - trackH / 2),
                        size = Size(w * progress, trackH)
                    )
                }
            }
        }
    }
}
