package com.davidsimba.vintbeats.feature.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid

@Composable
fun PlaylistScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onPlayAll: (List<Track>) -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)

    val title = (uiState as? PlaylistUiState.Success)?.detail?.title.orEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is PlaylistUiState.Loading -> {
                CircularProgressIndicator(
                    color = VintageGray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is PlaylistUiState.Error -> {
                Text(
                    text = state.message,
                    color = VintageGrayMid,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            }

            is PlaylistUiState.Success -> {
                val detail = state.detail
                val subtitle = stringResource(R.string.playlist_count, detail.tracks.size)

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize().background(VintageBgDark)
                ) {
                    item {
                        CollectionHeader(
                            title = detail.title,
                            subtitle = subtitle,
                            imageUrl = detail.thumbnailUrl,
                            placeholderIcon = Icons.Rounded.LibraryMusic,
                            onPlayAll = if (detail.tracks.isNotEmpty()) {
                                { onPlayAll(detail.tracks) }
                            } else null
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(VintageBgDark)
                        ) {
                            Spacer(Modifier.height(6.dp))
                            detail.tracks.forEachIndexed { index, track ->
                                TrackCard(
                                    title = track.title,
                                    artist = track.artist,
                                    thumbnailUrl = track.albumImageUrl,
                                    onClick = {
                                        onTrackSelected(track, detail.tracks.drop(index + 1))
                                    }
                                )
                            }
                            Spacer(Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        CollectionAppBar(
            title = title,
            alpha = appBarAlpha,
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart).zIndex(1f)
        )
    }
}
