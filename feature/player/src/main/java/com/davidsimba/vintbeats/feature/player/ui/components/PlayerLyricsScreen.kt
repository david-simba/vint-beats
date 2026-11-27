package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.player.R
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

private val ActiveLineOffset = 120.dp
private val TopFade = 56.dp
private val BottomFade = 72.dp

@Composable
fun PlayerLyricsScreen(
    lines: List<LyricLine>,
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    isFavorite: Boolean,
    currentIndex: Int,
    track: Track?,
    backgroundColor: Color,
    onClose: () -> Unit,
    onMoreOptions: () -> Unit,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onOpenQueue: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        LyricsHeader(
            title = track?.title.orEmpty(),
            artist = track?.artist.orEmpty(),
            onClose = onClose,
            onMoreOptions = onMoreOptions,
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                isLoading -> LyricsSkeleton()
                lines.isEmpty() -> Text(
                    text = stringResource(R.string.lyrics_none),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                )
                else -> LyricsList(
                    lines = lines,
                    currentIndex = currentIndex,
                    onSeek = onSeek,
                )
            }
        }

        LyricsControls(
            positionMs = positionMs,
            durationMs = durationMs,
            isPlaying = isPlaying,
            isFavorite = isFavorite,
            backgroundColor = backgroundColor,
            onSeek = onSeek,
            onTogglePlayPause = onTogglePlayPause,
            onToggleFavorite = onToggleFavorite,
            onSkipPrevious = onSkipPrevious,
            onSkipNext = onSkipNext,
            onOpenQueue = onOpenQueue,
        )
    }
}

@Composable
private fun LyricsHeader(
    title: String,
    artist: String,
    onClose: () -> Unit,
    onMoreOptions: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose, modifier = Modifier.size(44.dp)) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(R.string.lyrics_close),
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.lyrics_label),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
            )
            Text(
                text = if (artist.isBlank()) title else "$title · $artist",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onMoreOptions, modifier = Modifier.size(44.dp)) {
            Icon(
                imageVector = Icons.Rounded.MoreHoriz,
                contentDescription = stringResource(R.string.lyrics_more_options),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun LyricsList(
    lines: List<LyricLine>,
    currentIndex: Int,
    onSeek: (Long) -> Unit,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val targetPx = with(density) { ActiveLineOffset.toPx() }

    // El scroll automático se pausa si el usuario desliza y se retoma a los 3 s sin interacción.
    var paused by remember { mutableStateOf(false) }
    val programmatic = remember { booleanArrayOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { inProgress ->
            if (inProgress && !programmatic[0]) paused = true
        }
    }
    LaunchedEffect(paused, listState.isScrollInProgress) {
        if (paused && !listState.isScrollInProgress) {
            delay(3000.milliseconds)
            paused = false
        }
    }
    LaunchedEffect(currentIndex, paused) {
        if (paused || currentIndex < 0) return@LaunchedEffect
        val info = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == currentIndex }
        programmatic[0] = true
        try {
            if (info != null) {
                // La posición reportada no incluye el padding superior de la lista.
                val delta = info.offset - (targetPx - listState.layoutInfo.beforeContentPadding)
                if (abs(delta) > 1f) {
                    listState.animateScrollBy(delta, tween(600, easing = LinearOutSlowInEasing))
                }
            } else {
                listState.scrollToItem(currentIndex)
            }
        } finally {
            programmatic[0] = false
        }
    }

    val goToLine = stringResource(R.string.lyrics_go_to_line)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    val top = TopFade.toPx()
                    val bottom = BottomFade.toPx()
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.Transparent, 1f to Color.Black, startY = 0f, endY = top
                        ),
                        size = Size(size.width, top),
                        blendMode = BlendMode.DstIn,
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.Black, 1f to Color.Transparent,
                            startY = size.height - bottom, endY = size.height
                        ),
                        topLeft = Offset(0f, size.height - bottom),
                        size = Size(size.width, bottom),
                        blendMode = BlendMode.DstIn,
                    )
                },
        ) {
            itemsIndexed(lines) { index, line ->
                val alpha by animateFloatAsState(
                    targetValue = when {
                        index == currentIndex -> 1f
                        index > currentIndex -> 0.55f
                        else -> 0.28f
                    },
                    animationSpec = tween(400),
                    label = "lyric_alpha_$index",
                )
                Text(
                    text = line.text,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClickLabel = goToLine, role = Role.Button) { onSeek(line.timeMs) }
                        .padding(horizontal = 24.dp)
                        .graphicsLayer { this.alpha = alpha }
                )
            }
        }

        AnimatedVisibility(
            visible = paused && currentIndex >= 0,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        ) {
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(role = Role.Button) { paused = false }
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.lyrics_back_to_current),
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LyricsSkeleton() {
    val alpha by rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.12f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "skeleton_alpha_screen",
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        listOf(0.8f, 0.6f, 0.9f, 0.5f, 0.75f, 0.55f, 0.85f).forEach { fraction ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(22.dp)
                    .background(Color.White.copy(alpha = alpha), RoundedCornerShape(11.dp))
            )
        }
    }
}

@Composable
private fun LyricsControls(
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    isFavorite: Boolean,
    backgroundColor: Color,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    onOpenQueue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp),
    ) {
        LyricsProgressBar(positionMs = positionMs, durationMs = durationMs, onSeek = onSeek)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = formatTime(positionMs),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatTime(durationMs),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onToggleFavorite, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) R.string.lyrics_unlike else R.string.lyrics_like
                    ),
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
            IconButton(onClick = onSkipPrevious, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = stringResource(R.string.lyrics_previous),
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(role = Role.Button, onClick = onTogglePlayPause),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(
                        if (isPlaying) R.string.lyrics_pause else R.string.lyrics_play
                    ),
                    tint = backgroundColor,
                    modifier = Modifier.size(36.dp),
                )
            }
            IconButton(onClick = onSkipNext, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = stringResource(R.string.lyrics_next),
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
            IconButton(onClick = onOpenQueue, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                    contentDescription = stringResource(R.string.lyrics_queue),
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}

/** Barra de progreso blanca: pista al 25%, relleno y punto de 14dp. Se puede arrastrar. */
@Composable
internal fun LyricsProgressBar(
    positionMs: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(0f) }
    val fraction = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val display = if (isDragging) dragFraction else fraction
    val description = stringResource(R.string.lyrics_progress)
    val state = stringResource(
        R.string.lyrics_progress_state,
        formatTime((display * durationMs).toLong()),
        formatTime(durationMs),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .semantics {
                contentDescription = description
                stateDescription = state
            }
            .pointerInput(durationMs) {
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
            .drawWithContent {
                drawContent()
                val trackH = 4.dp.toPx()
                val thumbR = 7.dp.toPx()
                val cy = size.height / 2f
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.25f),
                    topLeft = Offset(0f, cy - trackH / 2),
                    size = Size(size.width, trackH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackH / 2),
                )
                val thumbX = (size.width * display).coerceIn(thumbR, size.width - thumbR)
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(0f, cy - trackH / 2),
                    size = Size(thumbX, trackH),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackH / 2),
                )
                drawCircle(color = Color.White, radius = thumbR, center = Offset(thumbX, cy))
            }
    )
}

internal fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
