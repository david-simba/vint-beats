package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRedLight
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.player.R
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableColumn
import kotlin.math.roundToInt

private const val CollapsedCount = 4
private val Secondary = Color(0xFFA1A1AA)

/**
 * Tarjeta "A continuación": las 4 siguientes canciones de la cola. "Ver cola completa" la despliega
 * en el mismo lugar; las filas se reordenan con el asa y se quitan deslizando.
 */
@Composable
fun PlayerQueueCard(
    queue: List<Track>,
    isQueueLoading: Boolean,
    backgroundColor: Color,
    onTrackClick: (Track) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
    onRemove: (Track) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var localQueue by remember { mutableStateOf(queue) }
    LaunchedEffect(queue) { localQueue = queue }

    PlayerSectionCard(backgroundColor = backgroundColor, modifier = modifier) {
        Text(
            text = stringResource(R.string.queue_up_next),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(8.dp))

        if (localQueue.isEmpty()) {
            Text(
                text = stringResource(if (isQueueLoading) R.string.queue_loading else R.string.queue_empty),
                color = Secondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
        } else {
            val visible = if (expanded) localQueue else localQueue.take(CollapsedCount)
            ReorderableColumn(
                list = visible,
                onSettle = { from, to ->
                    localQueue = localQueue.toMutableList().apply { add(to, removeAt(from)) }
                    onReorder(from, to)
                },
            ) { _, track, _ ->
                SwipeToRemoveBox(
                    backgroundColor = backgroundColor,
                    onRemove = {
                        localQueue = localQueue.toMutableList().also { it.remove(track) }
                        onRemove(track)
                    },
                ) {
                    QueueRow(
                        track = track,
                        onClick = { onTrackClick(track) },
                        handleModifier = Modifier.draggableHandle(),
                    )
                }
            }
            if (localQueue.size > CollapsedCount) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clickable(role = Role.Button) { expanded = !expanded },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(if (expanded) R.string.queue_show_less else R.string.queue_view_full),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueRow(
    track: Track,
    onClick: () -> Unit,
    handleModifier: Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = track.albumImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f)) {
            androidx.compose.foundation.layout.Column {
                Text(
                    text = track.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = track.artist,
                    color = Secondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        val reorderLabel = stringResource(R.string.queue_reorder)
        Box(
            modifier = handleModifier
                .size(44.dp)
                .semantics { contentDescription = reorderLabel },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = null,
                tint = Color(0xFF71717A),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun SwipeToRemoveBox(
    backgroundColor: Color,
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
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(22.dp)
                    .graphicsLayer {
                        alpha = if (width <= 0f) 0f else (-offsetX.value / width * 3f).coerceIn(0f, 1f)
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
                            scope.launch { offsetX.animateTo(0f, spring(stiffness = 400f)) }
                        }
                    }
                )
        ) {
            Box(modifier = Modifier.matchParentSize().background(backgroundColor))
            content()
        }
    }
}
