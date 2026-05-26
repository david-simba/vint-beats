package com.davidsimba.vintbeats.feature.artist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.zIndex
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistAlbumsList
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistHeader
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistTopSongItem
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistTopSongsEmpty
import androidx.compose.foundation.background
import com.davidsimba.vintbeats.shared.components.SectionLabel
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayCool
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

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
    val parallaxOffset by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == 0 }
                ?.offset?.toFloat() ?: 0f
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ArtistUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VintageGrayCool
                )
            }

            is ArtistUiState.Error -> {
                Text(
                    text = state.message,
                    color = VintageWhitePure,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            }

            is ArtistUiState.Success -> {
                LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
                    item {
                        ArtistHeader(
                            artist = state.artist,
                            hasTopTracks = state.topTracks.isNotEmpty(),
                            isLoadingPlay = isLoadingPlay,
                            parallaxOffset = parallaxOffset,
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
                                SectionLabel("Top Songs")
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
                                SectionLabel("Albums")
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

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .zIndex(1f)
                .statusBarsPadding()
                .padding(8.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "Back",
                tint = VintageWhitePure,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
