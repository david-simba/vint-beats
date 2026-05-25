package com.davidsimba.vintbeats.feature.player.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.shared.components.BottomSheetMenuItem
import com.davidsimba.vintbeats.shared.components.BottomSheet
import kotlinx.coroutines.launch
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerBackground
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerControls
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerQueueSheet
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTopBar
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTrackInfo
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerLyricsCard
import com.davidsimba.vintbeats.shared.components.EqualizerBars
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageRedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    viewModel: PlaybackViewModel
) {
    val currentSavedTrack by viewModel.currentSavedTrack.collectAsStateWithLifecycle()
    val unsavedTrack by viewModel.unsavedTrack.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val positionMs by viewModel.positionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val syncedLyrics by viewModel.syncedLyrics.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    var showOptionsSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val queueSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val offsetX = remember { Animatable(0f) }
    var componentWidth by remember { mutableFloatStateOf(0f) }

    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val isPlaying = playerState is PlayerState.Playing
    val isLoading = playerState is PlayerState.Loading

    val trackForCard: Track? = if (isSaved) {
        currentSavedTrack?.let {
            Track(
                id = it.trackId, title = it.trackTitle, artist = it.trackArtist,
                albumImageUrl = it.trackThumbnailUrl, previewUrl = null, durationText = it.trackDurationText
            )
        }
    } else {
        unsavedTrack
    }

    val nextTrack = queue.firstOrNull()
    val previousTrack = history.lastOrNull()
    val hasPrevious by rememberUpdatedState(previousTrack != null)

    val outerListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VintageBgDark)
            .onSizeChanged { componentWidth = it.width.toFloat() }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var cumulX = 0f
                    var cumulY = 0f
                    var isHorizontal: Boolean? = null

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        val dx = change.position.x - change.previousPosition.x
                        val dy = change.position.y - change.previousPosition.y
                        cumulX += dx
                        cumulY += dy

                        if (!change.pressed) {
                            if (isHorizontal == true) {
                                when {
                                    offsetX.value < -(componentWidth * 0.4f) -> {
                                        change.consume()
                                        scope.launch {
                                            offsetX.animateTo(-componentWidth, tween(150))
                                            viewModel.skipToNext()
                                            offsetX.snapTo(0f)
                                        }
                                    }
                                    offsetX.value > (componentWidth * 0.4f) -> {
                                        change.consume()
                                        scope.launch {
                                            offsetX.animateTo(componentWidth, tween(150))
                                            viewModel.skipToPrevious()
                                            offsetX.snapTo(0f)
                                        }
                                    }
                                    else -> {
                                        scope.launch {
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
                            }
                            break
                        }

                        if (isHorizontal == null) {
                            val absX = kotlin.math.abs(cumulX)
                            val absY = kotlin.math.abs(cumulY)
                            if (absX > viewConfiguration.touchSlop || absY > viewConfiguration.touchSlop) {
                                isHorizontal = absX >= absY
                                if (isHorizontal == false) scope.launch { offsetX.snapTo(0f) }
                            }
                        }

                        if (isHorizontal == true) {
                            val newOffset = (offsetX.value + dx).coerceIn(
                                -componentWidth,
                                if (hasPrevious) componentWidth else 0f
                            )
                            scope.launch { offsetX.snapTo(newOffset) }
                            change.consume()
                        }
                    }
                }
            }
    ) {
        LazyColumn(
            state = outerListState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Box(modifier = Modifier.fillParentMaxSize()) {
                    PlayerBackground(
                        currentImageUrl = trackForCard?.albumImageUrl,
                        nextImageUrl = nextTrack?.albumImageUrl,
                        previousImageUrl = previousTrack?.albumImageUrl,
                        offsetX = offsetX.value,
                        componentWidth = componentWidth,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    PlayerTopBar(
                        onBack = onBack,
                        onQueueOpen = { showQueueSheet = true },
                        onMoreOptions = { showOptionsSheet = true }
                    )

                    Spacer(Modifier.weight(1f))

                    trackForCard?.let {
                        PlayerTrackInfo(
                            title = it.title,
                            artist = it.artist,
                            isFavorite = isFavorite,
                            onToggleFavorite = { isFavorite = !isFavorite }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    EqualizerBars(
                        isPlaying = isPlaying,
                        color = VintageRedLight.copy(alpha = 0.8f),
                        maxHeight = 18.dp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    PlayerControls(
                        isPlaying = isPlaying,
                        isLoading = isLoading,
                        positionMs = positionMs,
                        durationMs = durationMs,
                        accentColor = VintageRedLight,
                        onSeek = viewModel::seekTo,
                        onTogglePlayPause = viewModel::togglePlayPause,
                        onSkipPrevious = viewModel::skipToPrevious,
                        onSkipNext = viewModel::skipToNext,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(Modifier.height(32.dp))
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(VintageBgDark)
                ) {
                    PlayerLyricsCard(
                        lines = syncedLyrics,
                        positionMs = positionMs,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 40.dp)
                    )
                }
            }
        }

        if (showOptionsSheet) {
            BottomSheet(
                onDismiss = { showOptionsSheet = false },
                sheetState = sheetState
            ) {
                if (!isSaved) {
                    BottomSheetMenuItem(
                        label = "Download",
                        icon = Icons.Rounded.Download,
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showOptionsSheet = false
                                viewModel.downloadCurrentTrack()
                            }
                        }
                    )
                }
            }
        }

        if (showQueueSheet) {
            BottomSheet(
                onDismiss = { showQueueSheet = false },
                sheetState = queueSheetState
            ) {
                PlayerQueueSheet(
                    currentTrack = trackForCard,
                    queue = queue,
                    isPlaying = isPlaying,
                    onTrackClick = { track ->
                        scope.launch { queueSheetState.hide() }.invokeOnCompletion {
                            showQueueSheet = false
                            viewModel.skipToQueueTrack(track)
                        }
                    },
                    onReorder = viewModel::reorderQueue
                )
            }
        }
    }
}
