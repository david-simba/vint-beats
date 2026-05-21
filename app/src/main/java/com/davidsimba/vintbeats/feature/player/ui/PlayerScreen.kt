package com.davidsimba.vintbeats.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.feature.player.components.PlayerControls
import com.davidsimba.vintbeats.feature.player.components.PlayerTopBar
import com.davidsimba.vintbeats.feature.search.domain.Track
import com.davidsimba.vintbeats.shared.components.LyricsCard
import com.davidsimba.vintbeats.shared.components.TrackCard
import com.davidsimba.vintbeats.shared.components.cassette.CassetteView
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight

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

    val isPlaying = playerState is PlayerState.Playing
    val isLoading = playerState is PlayerState.Loading

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(VintageBgDark, VintageBgBase)))
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerTopBar(isSaved = isSaved, onBack = onBack, onSave = onSave)

            Column(
                modifier = Modifier.padding(
                    top = 16.dp,
                    end = 24.dp,
                    bottom = 32.dp,
                    start = 24.dp
                )
            ) {
                CassetteView(
                    isPlaying = isPlaying,
                    isFloating = true,
                    cassetteColor = cassette?.cassetteColor ?: VintageBlackMid,
                    lineColor = cassette?.lineColor ?: VintageRedLight,
                    drawRainbow = cassette?.isRainbow ?: true
                )

                Spacer(Modifier.height(32.dp))

                trackForCard?.let {
                    TrackCard(track = it)
                }

                Spacer(Modifier.height(32.dp))

                PlayerControls(
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    positionMs = positionMs,
                    durationMs = durationMs,
                    onSeek = viewModel::seekTo,
                    onTogglePlayPause = viewModel::togglePlayPause
                )
            }
        }

        lyrics?.let {
            LyricsCard(lyrics = it)
            Spacer(Modifier.height(32.dp))
        }
    }
}
