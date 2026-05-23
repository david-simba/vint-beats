package com.davidsimba.vintbeats.feature.player.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.shared.components.BottomSheetMenuItem
import com.davidsimba.vintbeats.shared.components.BottomSheet
import kotlinx.coroutines.launch
import com.davidsimba.vintbeats.feature.player.ui.components.LyricsCard
import com.davidsimba.vintbeats.shared.components.background.Background
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerBottomNav
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerControls
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerEffectsCard
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerQueueCard
import com.davidsimba.vintbeats.feature.player.ui.components.PlayerTopBar
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
    var topContentHeightPx by remember { mutableIntStateOf(0) }
    var showOptionsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val availableHeight = maxHeight

        Background(
            thumbnailUrl = trackForCard?.albumImageUrl,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { topContentHeightPx = it.height },
                horizontalAlignment = Alignment.Start
            ) {
                PlayerTopBar(onBack = onBack, onMoreOptions = { showOptionsSheet = true })

                Column(
                    modifier = Modifier.padding(
                        top = 16.dp,
                        end = 24.dp,
                        bottom = 16.dp,
                        start = 24.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = trackForCard?.albumImageUrl,
                        contentDescription = trackForCard?.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .heightIn(max = 320.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    trackForCard?.let {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = it.title,
                                color = VintageWhitePure,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = it.artist,
                                color = VintageGrayMid,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    PlayerControls(
                        isPlaying = isPlaying,
                        isLoading = isLoading,
                        positionMs = positionMs,
                        durationMs = durationMs,
                        accentColor = VintageRedLight,
                        onSeek = viewModel::seekTo,
                        onTogglePlayPause = viewModel::togglePlayPause
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }

            val navBarHeight = 80.dp
            val tabCardModifier = with(LocalDensity.current) {
                val minHeight = (availableHeight - topContentHeightPx.toDp() - navBarHeight)
                    .coerceAtLeast(0.dp)
                Modifier.fillMaxWidth().heightIn(min = minHeight)
            }

            when (selectedTab) {
                PlayerTab.Lyrics -> LyricsCard(lyrics = lyrics, modifier = tabCardModifier)
                PlayerTab.Player -> PlayerEffectsCard(modifier = tabCardModifier)
                PlayerTab.Queue -> PlayerQueueCard(
                    currentTrack = trackForCard,
                    queue = queue,
                    onTrackClick = viewModel::skipToQueueTrack,
                    onReorder = viewModel::reorderQueue,
                    modifier = tabCardModifier
                )
            }

            Spacer(Modifier.height(navBarHeight))
        }

        PlayerBottomNav(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

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
    }
}
