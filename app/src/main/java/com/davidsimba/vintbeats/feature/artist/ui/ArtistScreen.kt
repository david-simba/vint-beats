package com.davidsimba.vintbeats.feature.artist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistAlbumsList
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistHeader
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistTopSongItem
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistTopSongsEmpty
import com.davidsimba.vintbeats.shared.components.SectionLabel
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun ArtistScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    onPlayArtist: (List<Track>) -> Unit,
    onAlbumSelected: (Album) -> Unit,
    viewModel: ArtistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoadingPlay by viewModel.isLoadingPlay.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    val rawAppBarAlpha by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) return@derivedStateOf 1f
            val item = lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == 0 } ?: return@derivedStateOf 0f
            val offset = lazyListState.firstVisibleItemScrollOffset.toFloat()
            val fadeStart = item.size * 0.55f
            val fadeEnd = item.size * 0.7f
            ((offset - fadeStart) / (fadeEnd - fadeStart)).coerceIn(0f, 1f)
        }
    }
    val appBarAlpha = rawAppBarAlpha

    val artistName = (uiState as? ArtistUiState.Success)?.artist?.name.orEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ArtistUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VintageGray
                )
            }

            is ArtistUiState.Error -> {
                Text(
                    text = state.message,
                    color = VintageWhite,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            }

            is ArtistUiState.Success -> {
                LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize().background(VintageBgDark)) {
                    item {
                        ArtistHeader(
                            artist = state.artist,
                            hasTopTracks = state.topTracks.isNotEmpty(),
                            isLoadingPlay = isLoadingPlay,
                            onPlay = {
                                viewModel.loadPlayQueue { tracks -> onPlayArtist(tracks) }
                            }
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(VintageBgDark)
                        ) {
                            if (state.topTracks.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                SectionLabel(stringResource(R.string.artist_top_songs))
                                state.topTracks.forEachIndexed { index, track ->
                                    ArtistTopSongItem(
                                        index = index + 1,
                                        track = track,
                                        onClick = { onTrackSelected(track) }
                                    )
                                }
                            } else {
                                ArtistTopSongsEmpty()
                            }

                            if (state.albums.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                SectionLabel(stringResource(R.string.artist_albums))
                                ArtistAlbumsList(
                                    albums = state.albums,
                                    onAlbumClick = { album -> onAlbumSelected(album) }
                                )
                            }

                            Spacer(Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .zIndex(1f)
                .background(VintageBgDark.copy(alpha = appBarAlpha))
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = VintageWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = artistName,
                    color = VintageWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(appBarAlpha)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .alpha(appBarAlpha),
                color = VintageWhite.copy(alpha = 0.12f)
            )
        }
    }
}
