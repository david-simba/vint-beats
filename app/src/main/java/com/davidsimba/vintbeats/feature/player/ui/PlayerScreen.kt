package com.davidsimba.vintbeats.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.shared.components.BottomSheetMenuItem
import com.davidsimba.vintbeats.shared.components.BottomSheet
import kotlinx.coroutines.launch
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerControls
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerQueueSheet
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTopBar
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTrackInfo
import com.davidsimba.vintbeats.shared.components.EqualizerBars
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

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
    val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(PlayerTab.Queue) }
    var showOptionsSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    val swipeThresholdPx = with(LocalDensity.current) { 80.dp.toPx() }
    val sheetState = rememberModalBottomSheetState()
    val queueSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                            viewModel.skipToNext()
                        } else if (triggered) {
                            change.consume()
                        }
                    }
                }
            }
    ) {
        // Album art as full-screen background
        AsyncImage(
            model = trackForCard?.albumImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay — keeps image clear, text readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.35f),
                        0.6f to Color.Black.copy(alpha = 0.5f),
                        1f to Color.Black.copy(alpha = 0.85f)
                    )
                )
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
                onSkipNext = viewModel::skipToNext,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(32.dp))

            // TODO: bottom nav + cards (logic ready, UI coming)
            /*
            PlayerBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            when (selectedTab) {
                PlayerTab.Lyrics -> LyricsCard(lyrics = lyrics)
                PlayerTab.Player -> PlayerEffectsCard()
                PlayerTab.Queue -> PlayerQueueSheet(
                    currentTrack = trackForCard,
                    queue = queue,
                    onTrackClick = viewModel::skipToQueueTrack,
                    onReorder = viewModel::reorderQueue
                )
            }
            */
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
