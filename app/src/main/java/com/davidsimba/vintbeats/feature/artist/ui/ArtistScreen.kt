package com.davidsimba.vintbeats.feature.artist.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistAlbumsList
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistTopSongItem
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistTopSongsEmpty
import com.davidsimba.vintbeats.shared.TrackActionsViewModel
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.SectionLabel
import com.davidsimba.vintbeats.shared.components.TrackOptionsBottomSheet
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.core.util.toHighRes
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    onPlayArtist: (List<Track>) -> Unit,
    onAlbumSelected: (Album) -> Unit,
    viewModel: ArtistViewModel = hiltViewModel(),
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoadingPlay by viewModel.isLoadingPlay.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val favoriteTrackIds by trackActionsViewModel.favoriteTrackIds.collectAsStateWithLifecycle()
    val downloadedTrackIds by trackActionsViewModel.downloadedTrackIds.collectAsStateWithLifecycle()
    val downloadingTrackId by trackActionsViewModel.downloadingTrackId.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)
    val artistName = (uiState as? ArtistUiState.Success)?.artist?.name.orEmpty()

    var selectedTrack by remember { mutableStateOf<Track?>(null) }

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
                        CollectionHeader(
                            title = state.artist.name,
                            subtitle = null,
                            imageUrl = state.artist.thumbnailUrl.toHighRes(),
                            placeholderIcon = Icons.Rounded.Person,
                            imageAlignment = Alignment.TopCenter,
                            onPlayAll = if (state.topTracks.isNotEmpty() && !isLoadingPlay) {
                                { viewModel.loadPlayQueue { tracks -> onPlayArtist(tracks) } }
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
                            if (state.topTracks.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                SectionLabel(stringResource(R.string.artist_top_songs))
                                state.topTracks.forEachIndexed { index, track ->
                                    ArtistTopSongItem(
                                        index = index + 1,
                                        track = track,
                                        onClick = { onTrackSelected(track) },
                                        onMenuClick = { selectedTrack = track }
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

        CollectionAppBar(
            title = artistName,
            alpha = appBarAlpha,
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart).zIndex(1f),
            trailingContent = {
                if (uiState is ArtistUiState.Success) {
                    IconButton(onClick = viewModel::toggleSave) {
                        Icon(
                            imageVector = if (isSaved) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isSaved) VintageRedLight else VintageWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        )
    }

    selectedTrack?.let { track ->
        TrackOptionsBottomSheet(
            isFavorite = track.id in favoriteTrackIds,
            isDownloaded = track.id in downloadedTrackIds,
            isDownloading = downloadingTrackId == track.id,
            onDownload = {
                trackActionsViewModel.downloadTrack(track)
                selectedTrack = null
            },
            onToggleFavorite = {
                trackActionsViewModel.toggleFavorite(track)
                selectedTrack = null
            },
            onAddToPlaylist = {},
            onDismiss = { selectedTrack = null }
        )
    }
}
