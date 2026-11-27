package com.davidsimba.vintbeats.feature.player.ui

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.util.lerp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import kotlin.math.abs
import com.davidsimba.vintbeats.core.designsystem.R as DsR

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.feature.player.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.CancellationException
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.designsystem.components.feedback.BottomSheetMenuItem
import com.davidsimba.vintbeats.core.designsystem.components.feedback.BottomSheet
import kotlinx.coroutines.launch
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerBackground
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerCover
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerControls
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerQueueSheet
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTopBar
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTrackInfo
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerLyricsCard
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerLyricsScreen
import com.davidsimba.vintbeats.core.model.Track
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.davidsimba.vintbeats.core.designsystem.components.layout.EqualizerBars
import com.davidsimba.vintbeats.core.designsystem.components.background.rememberPaletteColor
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    onArtistSelected: (browseId: String) -> Unit,
    onAddToPlaylist: (Track) -> Unit = {},
    onPlayingFromClick: (() -> Unit)? = null,
    viewModel: PlaybackViewModel,
) {
    val currentTrack by viewModel.currentTrack.collectAsStateWithLifecycle()
    val hasLocalFile by viewModel.hasLocalFile.collectAsStateWithLifecycle()
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
    val showEqualizer by viewModel.equalizerEnabled.collectAsStateWithLifecycle()
    val coverAsBackground by viewModel.coverAsBackground.collectAsStateWithLifecycle()

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

    val trackForCard: Track? = currentTrack

    val nextTrack = queue.firstOrNull()
    val previousTrack = history.lastOrNull()

    val paletteColor = rememberPaletteColor(trackForCard?.albumImageUrl)
    val cardBgColor = remember(paletteColor) {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(paletteColor.toArgb(), hsl)
        hsl[2] = (hsl[2] + 0.06f).coerceAtMost(0.35f)
        Color(ColorUtils.HSLToColor(hsl))
    }

    val currentLyricIndex by remember(syncedLyrics) {
        derivedStateOf {
            if (syncedLyrics.isEmpty()) -1
            else syncedLyrics.indexOfLast { it.timeMs <= positionMs }
        }
    }

    val outerScrollState = rememberScrollState()

    // Portada cuadrada: crece hasta ser un header de ancho completo mientras se hace scroll; después sube con el contenido.
    val density = LocalDensity.current
    var coverX by remember { mutableFloatStateOf(0f) }
    var coverY0 by remember { mutableFloatStateOf(0f) } // posición con el scroll en 0
    var coverSide by remember { mutableFloatStateOf(0f) }
    // Distancia de scroll que dura el crecimiento; si el recorrido es corto, se usa todo.
    var rootHeight by remember { mutableFloatStateOf(0f) }
    var lyricsBlockPx by remember { mutableFloatStateOf(0f) }
    var groupPx by remember { mutableFloatStateOf(0f) }
    var infoPx by remember { mutableFloatStateOf(0f) }
    // Recorrido hasta que la portada es header y el reproductor queda debajo de ella.
    // Solo el título/artista sube sobre la imagen; progreso y controles quedan justo debajo de ella.
    val growDistance: () -> Float = { maxOf(rootHeight - componentWidth - (groupPx - infoPx), 1f) }
    val growEnd: () -> Float = { minOf(growDistance(), outerScrollState.maxValue.toFloat()) }
    val growProgress: () -> Float = {
        val end = growEnd()
        if (end <= 1f) 0f else (outerScrollState.value / end).coerceIn(0f, 1f)
    }

    // Al soltar el scroll, la portada se fija en uno de los dos estados: cuadrada o header completo.
    LaunchedEffect(outerScrollState, coverAsBackground) {
        snapshotFlow { outerScrollState.isScrollInProgress }
            .filter { inProgress -> !inProgress }
            .collect {
                val value = outerScrollState.value
                val end = growEnd()
                if (!coverAsBackground && end > 1f && value > 0 && value < end) {
                    try {
                        outerScrollState.animateScrollTo(
                            if (value < end * 0.25f) 0 else end.toInt(),
                            tween(250),
                        )
                    } catch (e: CancellationException) {
                        // Un gesto del usuario interrumpió la animación: solo se cancela ese salto, no el efecto.
                        currentCoroutineContext().ensureActive()
                    }
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(paletteColor)
            .onSizeChanged {
                componentWidth = it.width.toFloat()
                rootHeight = it.height.toFloat()
            }
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
        if (!coverAsBackground && coverSide > 0f) {
            PlayerCover(
                currentImageUrl = trackForCard?.albumImageUrl,
                nextImageUrl = nextTrack?.albumImageUrl,
                previousImageUrl = previousTrack?.albumImageUrl,
                offsetX = offsetX.value,
                componentWidth = componentWidth,
                shape = RectangleShape,
                elevated = false,
                modifier = Modifier
                    .size(with(density) { coverSide.toDp() })
                    .graphicsLayer {
                        val f = growProgress()
                        val size = lerp(coverSide, componentWidth, f)
                        val scale = size / coverSide
                        // Pasado el crecimiento, la portada sube con el resto del contenido.
                        val extraScroll = maxOf(0f, outerScrollState.value - growEnd())
                        transformOrigin = TransformOrigin(0f, 0f)
                        scaleX = scale
                        scaleY = scale
                        translationX = lerp(coverX, 0f, f)
                        translationY = lerp(coverY0, 0f, f) - extraScroll
                        // Esquinas: 12dp en reposo, rectas en el header (en coordenadas locales).
                        shape = RoundedCornerShape(lerp(12.dp.toPx(), 0f, f))
                        clip = true
                    }
            )
            // Degradado al final de la imagen, que aparece al crecer, para fundirla con el fondo.
            val fadeHeightPx = componentWidth * 0.4f
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { fadeHeightPx.toDp() })
                    .graphicsLayer {
                        val f = growProgress()
                        val bottom = lerp(coverY0 + coverSide, componentWidth, f)
                        translationY = bottom - fadeHeightPx - maxOf(0f, outerScrollState.value - growEnd())
                        alpha = f
                    }
                    .background(Brush.verticalGradient(listOf(Color.Transparent, paletteColor)))
            )
        }

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
                    if (coverAsBackground) {
                        PlayerBackground(
                            currentImageUrl = trackForCard?.albumImageUrl,
                            nextImageUrl = nextTrack?.albumImageUrl,
                            previousImageUrl = previousTrack?.albumImageUrl,
                            offsetX = offsetX.value,
                            componentWidth = componentWidth,
                            backgroundColor = paletteColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.graphicsLayer { alpha = if (coverAsBackground) 1f else 0f }) {
                            PlayerTopBar(
                                onBack = onBack,
                                onMoreOptions = { showOptionsSheet = true },
                                playingFromName = playingFrom?.name,
                                onPlayingFromClick = onPlayingFromClick
                            )
                        }

                        if (coverAsBackground) {
                            Spacer(Modifier.weight(1f))
                        } else {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Spacer(
                                    Modifier
                                        .size(minOf(maxWidth, maxHeight))
                                        .onGloballyPositioned { coords ->
                                            val pos = coords.positionInRoot()
                                            val y0 = pos.y + outerScrollState.value
                                            if (abs(coverX - pos.x) > 1f) coverX = pos.x
                                            if (abs(coverY0 - y0) > 1f) coverY0 = y0
                                            if (abs(coverSide - coords.size.width) > 1f) coverSide = coords.size.width.toFloat()
                                        }
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onSizeChanged { groupPx = it.height.toFloat() },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            trackForCard?.let {
                                Box(Modifier.onSizeChanged { infoPx = it.height.toFloat() }) {
                                PlayerTrackInfo(
                                    title = it.title,
                                    artist = it.artist,
                                    isFavorite = isFavorite,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onQueueOpen = { showQueueSheet = true },
                                    onArtistClick = it.artistId?.let { id -> { onArtistSelected(id) } }
                                )
                                }
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
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(paletteColor)
                        .onSizeChanged { lyricsBlockPx = it.height.toFloat() }
                ) {
                    PlayerLyricsCard(
                        lines = syncedLyrics,
                        isLoading = isLoadingLyrics,
                        currentIndex = currentLyricIndex,
                        cardBgColor = cardBgColor,
                        onExpand = { showLyricsScreen = true },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp, bottom = 40.dp)
                    )
                }
                if (!coverAsBackground) {
                    // Completa el recorrido para que el scroll termine con las letras pegadas bajo la portada.
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(with(density) { (growDistance() - lyricsBlockPx).coerceAtLeast(0f).toDp() })
                    )
                }
            }
        }

        if (!coverAsBackground) {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .graphicsLayer { translationY = -maxOf(0f, outerScrollState.value - growEnd()) }
            ) {
                PlayerTopBar(
                    onBack = onBack,
                    onMoreOptions = { showOptionsSheet = true },
                    playingFromName = playingFrom?.name,
                    onPlayingFromClick = onPlayingFromClick
                )
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
                    currentIndex = currentLyricIndex,
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
                if (!hasLocalFile) {
                    BottomSheetMenuItem(
                        label = stringResource(
                            if (isDownloading) DsR.string.player_option_downloading
                            else DsR.string.player_option_download
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
                        if (isFavorite) DsR.string.action_remove_favorite else DsR.string.action_add_favorite
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
                    label = stringResource(DsR.string.action_add_to_playlist),
                    icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
                    enabled = trackForCard != null,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showOptionsSheet = false
                            trackForCard?.let(onAddToPlaylist)
                        }
                    }
                )
                BottomSheetMenuItem(
                    label = stringResource(
                        if (coverAsBackground) R.string.player_option_cover_square
                        else R.string.player_option_cover_background
                    ),
                    icon = if (coverAsBackground) Icons.Rounded.CropSquare else Icons.Rounded.Image,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showOptionsSheet = false
                            viewModel.toggleCoverStyle()
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
                            viewModel.toggleEqualizer()
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
                    onRemove = viewModel::removeFromQueue,
                )
            }
        }
    }

}
