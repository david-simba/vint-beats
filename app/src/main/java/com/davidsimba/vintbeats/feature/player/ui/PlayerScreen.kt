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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.feature.player.components.LyricsCard
import com.davidsimba.vintbeats.feature.player.components.PlayerBackground
import com.davidsimba.vintbeats.feature.player.components.PlayerBottomNav
import com.davidsimba.vintbeats.feature.player.components.PlayerControls
import com.davidsimba.vintbeats.feature.player.components.PlayerEffectsCard
import com.davidsimba.vintbeats.feature.player.components.PlayerQueueCard
import com.davidsimba.vintbeats.feature.player.components.PlayerTopBar
import com.davidsimba.vintbeats.feature.search.domain.Track
import com.davidsimba.vintbeats.shared.components.cassette.CassetteView
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun PlayerScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: PlaybackViewModel
) {
    val cassette by viewModel.currentCassette.collectAsStateWithLifecycle()
    val unsavedTrack by viewModel.unsavedTrack.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val positionMs by viewModel.positionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val lyrics by viewModel.lyrics.collectAsStateWithLifecycle()
    val queue by viewModel.queue.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(PlayerTab.Queue) }
    var topContentHeightPx by remember { mutableIntStateOf(0) }

    val isPlaying = playerState is PlayerState.Playing
    val isLoading = playerState is PlayerState.Loading
    val accentColor = if (cassette?.isRainbow != false) VintageRedLight else cassette!!.lineColor

    val trackForCard: Track? = if (isSaved) {
        cassette?.let {
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

        PlayerBackground(
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayerTopBar(isSaved = isSaved, onBack = onBack, onSave = onSave)

                Column(
                    modifier = Modifier.padding(
                        top = 16.dp,
                        end = 24.dp,
                        bottom = 16.dp,
                        start = 24.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CassetteView(
                        isPlaying = isPlaying,
                        isFloating = true,
                        cassetteColor = cassette?.cassetteColor ?: VintageBlackMid,
                        lineColor = cassette?.lineColor ?: VintageRedLight,
                        drawRainbow = cassette?.isRainbow ?: true,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(32.dp))

                    trackForCard?.let {
                        Text(
                            text = it.title,
                            color = VintageWhitePure,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = it.artist,
                            color = VintageGrayMid,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    PlayerControls(
                        isPlaying = isPlaying,
                        isLoading = isLoading,
                        positionMs = positionMs,
                        durationMs = durationMs,
                        accentColor = accentColor,
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
    }
}
