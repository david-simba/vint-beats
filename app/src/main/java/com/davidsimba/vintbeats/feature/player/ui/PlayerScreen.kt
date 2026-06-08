package com.davidsimba.vintbeats.feature.player.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.shared.components.BottomSheetMenuItem
import com.davidsimba.vintbeats.shared.components.BottomSheet
import com.davidsimba.vintbeats.shared.AddToPlaylistController
import com.davidsimba.vintbeats.shared.TrackActionsViewModel
import kotlinx.coroutines.launch
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerBackground
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerControls
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerQueueSheet
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTopBar
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTrackInfo
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerLyricsCard
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerLyricsScreen
import com.davidsimba.vintbeats.core.model.Track
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.davidsimba.vintbeats.shared.components.EqualizerBars
import com.davidsimba.vintbeats.shared.components.background.rememberPaletteColor
import com.davidsimba.vintbeats.shared.theme.VintageRedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    onArtistSelected: (browseId: String) -> Unit,
    onNavigateToAddToPlaylist: () -> Unit = {},
    onPlayingFromClick: (() -> Unit)? = null,
    viewModel: PlaybackViewModel,
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel()
) {
    val currentSavedTrack by viewModel.currentSavedTrack.collectAsStateWithLifecycle()
    val unsavedTrack by viewModel.unsavedTrack.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val positionMs by viewModel.positionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val syncedLyrics by viewModel.syncedLyrics.collectAsStateWithLifecycle()
    val isLoadingLyrics by viewModel.isLoadingLyrics.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()
    val isQueueLoading by viewModel.isQueueLoading.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    val playingFrom by viewModel.playingFrom.collectAsStateWithLifecycle()

    var showOptionsSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showLyricsScreen by remember { mutableStateOf(false) }
    val showEqualizer by PlayerPreferences.equalizerEnabled.collectAsStateWithLifecycle()

    BackHandler(enabled = showLyricsScreen) { showLyricsScreen = false }
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    val queueSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val offsetX = remember { Animatable(0f) }
    var componentWidth by remember { mutableFloatStateOf(0f) }

    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val shuffleEnabled by viewModel.shuffleEnabled.collectAsStateWithLifecycle()
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

    val paletteColor = rememberPaletteColor(trackForCard?.albumImageUrl)
    val cardBgColor = run {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(paletteColor.toArgb(), hsl)
        hsl[2] = (hsl[2] + 0.06f).coerceAtMost(0.35f)
        Color(ColorUtils.HSLToColor(hsl))
    }

    val outerScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(paletteColor)
            .onSizeChanged { componentWidth = it.width.toFloat() }
            .playerSwipeGesture(
                offsetX = offsetX,
                componentWidth = componentWidth,
                hasNext = nextTrack != null,
                hasPrevious = previousTrack != null,
                enabled = !showLyricsScreen,
                scope = scope,
                onSkipNext = viewModel::skipToNext,
                onSkipPrevious = { viewModel.skipToPrevious(force = true) },
            )
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenHeight = maxHeight
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(outerScrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight)
                ) {
                    PlayerBackground(
                        currentImageUrl = trackForCard?.albumImageUrl,
                        nextImageUrl = nextTrack?.albumImageUrl,
                        previousImageUrl = previousTrack?.albumImageUrl,
                        offsetX = offsetX.value,
                        componentWidth = componentWidth,
                        backgroundColor = paletteColor,
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
                            onMoreOptions = { showOptionsSheet = true },
                            playingFromName = playingFrom?.name,
                            onPlayingFromClick = onPlayingFromClick
                        )

                        Spacer(Modifier.weight(1f))

                        trackForCard?.let {
                            PlayerTrackInfo(
                                title = it.title,
                                artist = it.artist,
                                isFavorite = isFavorite,
                                onToggleFavorite = viewModel::toggleFavorite,
                                onQueueOpen = { showQueueSheet = true },
                                onArtistClick = it.artistId?.let { id -> { onArtistSelected(id) } }
                            )
                        }

                        AnimatedVisibility(
                            visible = showEqualizer,
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(300))
                        ) {
                            Spacer(Modifier.height(12.dp))
                            EqualizerBars(
                                isPlaying = isPlaying,
                                barCount = 16,
                                color = VintageRedLight.copy(alpha = 0.8f),
                                maxHeight = 18.dp,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }

                        PlayerControls(
                            isPlaying = isPlaying,
                            isLoading = isLoading,
                            positionMs = positionMs,
                            durationMs = durationMs,
                            accentColor = VintageRedLight,
                            repeatMode = repeatMode,
                            shuffleEnabled = shuffleEnabled,
                            onSeek = viewModel::seekTo,
                            onTogglePlayPause = viewModel::togglePlayPause,
                            onSkipPrevious = viewModel::skipToPrevious,
                            onSkipNext = viewModel::skipToNext,
                            onToggleRepeat = viewModel::toggleRepeatMode,
                            onToggleShuffle = viewModel::toggleShuffle,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(Modifier.height(32.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(paletteColor)
                ) {
                    PlayerLyricsCard(
                        lines = syncedLyrics,
                        isLoading = isLoadingLyrics,
                        positionMs = positionMs,
                        cardBgColor = cardBgColor,
                        onExpand = { showLyricsScreen = true },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 40.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showLyricsScreen,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardBgColor)
            ) {
                PlayerLyricsScreen(
                    lines = syncedLyrics,
                    isLoading = isLoadingLyrics,
                    positionMs = positionMs,
                    durationMs = durationMs,
                    isPlaying = isPlaying,
                    track = trackForCard,
                    onClose = { showLyricsScreen = false },
                    onSeek = viewModel::seekTo,
                    onTogglePlayPause = viewModel::togglePlayPause
                )
            }
        }

        if (showOptionsSheet) {
            BottomSheet(
                onDismiss = { showOptionsSheet = false },
                sheetState = sheetState
            ) {
                val hasLocalFile = isSaved && !currentSavedTrack?.audioFilePath.isNullOrEmpty()
                if (!hasLocalFile) {
                    BottomSheetMenuItem(
                        label = stringResource(
                            if (isDownloading) R.string.player_option_downloading
                            else R.string.player_option_download
                        ),
                        icon = Icons.Rounded.Download,
                        enabled = !isDownloading,
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showOptionsSheet = false
                                viewModel.downloadCurrentTrack()
                            }
                        }
                    )
                }
                BottomSheetMenuItem(
                    label = stringResource(
                        if (isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite
                    ),
                    icon = if (isFavorite) Icons.Rounded.HeartBroken else Icons.Rounded.FavoriteBorder,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showOptionsSheet = false
                            viewModel.toggleFavorite()
                        }
                    }
                )
                BottomSheetMenuItem(
                    label = stringResource(R.string.action_add_to_playlist),
                    icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
                    enabled = trackForCard != null,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showOptionsSheet = false
                            trackForCard?.let { AddToPlaylistController.pendingTrack = it }
                            onNavigateToAddToPlaylist()
                        }
                    }
                )
                BottomSheetMenuItem(
                    label = stringResource(
                        if (showEqualizer) R.string.player_option_equalizer_hide
                        else R.string.player_option_equalizer_show
                    ),
                    icon = Icons.Rounded.Equalizer,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showOptionsSheet = false
                            PlayerPreferences.toggleEqualizer()
                        }
                    }
                )
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
                    isQueueLoading = isQueueLoading,
                    onTrackClick = { track ->
                        scope.launch { queueSheetState.hide() }.invokeOnCompletion {
                            showQueueSheet = false
                            viewModel.skipToQueueTrack(track)
                        }
                    },
                    onReorder = viewModel::reorderQueue,
                )
            }
        }
    }

}
