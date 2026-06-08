package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import com.davidsimba.vintbeats.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.EqualizerBars
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableColumn
import kotlin.math.roundToInt

@Composable
fun PlayerQueueSheet(
    modifier: Modifier = Modifier,
    currentTrack: Track?,
    queue: List<Track>,
    isPlaying: Boolean,
    isQueueLoading: Boolean = false,
    onTrackClick: (Track) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
    onRemove: (Track) -> Unit,
) {
    var localQueue by remember { mutableStateOf(queue) }
    val scrollState = rememberScrollState()

    LaunchedEffect(queue) {
        localQueue = queue
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.queue_up_next),
            color = VintageWhiteWarm,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        currentTrack?.let {
            QueueTrackRow(
                track = it,
                isCurrentTrack = true,
                isPlaying = isPlaying,
                modifier = Modifier,
                onClick = null
            )
        }

        if (localQueue.isEmpty()) {
            if (isQueueLoading) {
                Text(
                    text = stringResource(R.string.queue_loading),
                    color = VintageWhite.copy(alpha = 0.4f),
                    fontSize = 14.sp
                )
            }
        } else {
            ReorderableColumn(
                list = localQueue,
                onSettle = { from, to ->
                    localQueue = localQueue.toMutableList().apply { add(to, removeAt(from)) }
                    onReorder(from, to)
                },
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) { _, track, _ ->
                SwipeToRemoveBox(
                    onRemove = {
                        localQueue = localQueue.toMutableList().also { it.remove(track) }
                        onRemove(track)
                    }
                ) {
                    QueueTrackRow(
                        track = track,
                        isCurrentTrack = false,
                        modifier = Modifier.draggableHandle(),
                        onClick = { onTrackClick(track) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeToRemoveBox(
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var width by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { width = it.width.toFloat() }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp))
                .background(VintageRedLight)
                .graphicsLayer {
                    alpha = if (width <= 0f || offsetX.value >= 0f) 0f
                            else (0.3f + (-offsetX.value / width).coerceIn(0f, 1f) * 0.6f).coerceAtMost(0.9f)
                },
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = null,
                tint = VintageWhite,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(22.dp)
                    .graphicsLayer {
                        alpha = if (width <= 0f) 0f
                                else (-offsetX.value / width * 3f).coerceIn(0f, 1f)
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val next = (offsetX.value + delta).coerceIn(-width, 0f)
                        scope.launch { offsetX.snapTo(next) }
                    },
                    onDragStopped = {
                        if (offsetX.value < -(width * 0.4f)) {
                            scope.launch {
                                offsetX.animateTo(-width, tween(180))
                                onRemove()
                                offsetX.snapTo(0f)
                            }
                        } else {
                            scope.launch {
                                offsetX.animateTo(0f, spring(stiffness = 400f))
                            }
                        }
                    }
                )
        ) {
            Box(modifier = Modifier.matchParentSize().background(VintageBgDark))
            content()
        }
    }
}

@Composable
private fun QueueTrackRow(
    track: Track,
    isCurrentTrack: Boolean,
    modifier: Modifier,
    onClick: (() -> Unit)?,
    isPlaying: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(top = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCurrentTrack) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.DragIndicator,
            contentDescription = null,
            tint = if (isCurrentTrack) VintageRedLight else VintageGrayMid,
            modifier = modifier.size(20.dp)
        )

        Spacer(Modifier.width(10.dp))

        AsyncImage(
            model = track.albumImageUrl,
            contentDescription = track.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(Modifier.width(12.dp))

        TrackInfo(
            title = track.title,
            artist = track.artist,
            modifier = Modifier.weight(1f),
            titleWeight = if (isCurrentTrack) FontWeight.SemiBold else FontWeight.Medium,
            titleColor = if (isCurrentTrack) VintageWhiteWarm else VintageWhite
        )

        Spacer(Modifier.width(8.dp))

        if (isCurrentTrack) {
            EqualizerBars(
                isPlaying = isPlaying,
                color = VintageRedLight,
                maxHeight = 12.dp,
                barCount = 4,
                modifier = Modifier.width(24.dp)
            )
        } else {
            Text(
                text = track.durationText,
                color = VintageGrayMid,
                fontSize = 11.sp
            )
        }
    }
}
