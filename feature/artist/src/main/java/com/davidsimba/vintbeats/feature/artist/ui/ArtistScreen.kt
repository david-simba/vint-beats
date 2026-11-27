package com.davidsimba.vintbeats.feature.artist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistActionRow
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistDiscographySection
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistListenSection
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistTop5Section
import com.davidsimba.vintbeats.feature.artist.ui.components.ArtistHeader
import com.davidsimba.vintbeats.core.ui.AddToPlaylistController
import com.davidsimba.vintbeats.core.common.QueueController
import com.davidsimba.vintbeats.core.ui.TrackActionsViewModel
import com.davidsimba.vintbeats.core.designsystem.components.track.TrackOptionsBottomSheet
import com.davidsimba.vintbeats.core.common.toHighRes
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGray
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    onPlayMixTrack: (Track, List<Track>) -> Unit = { _, _ -> },
    onMixPlaylistSelected: (playlistId: String) -> Unit = {},
    onPlayArtist: (List<Track>) -> Unit,
    onAlbumSelected: (Album) -> Unit,
    onNavigateToAddToPlaylist: () -> Unit = {},
    viewModel: ArtistViewModel = hiltViewModel(),
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoadingPlay by viewModel.isLoadingPlay.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val favoriteTrackIds by trackActionsViewModel.favoriteTrackIds.collectAsStateWithLifecycle()
    val downloadedTrackIds by trackActionsViewModel.downloadedTrackIds.collectAsStateWithLifecycle()
    val downloadingTrackIds by trackActionsViewModel.downloadingTrackIds.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    var selectedTrack by remember { mutableStateOf<Track?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Fondo)) {
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
                LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize().background(Fondo)) {
                    item {
                        ArtistHeader(
                            name = state.artist.name,
                            imageUrl = state.artist.thumbnailUrl.toHighRes(),
                            onBack = onBack,
                        )
                    }
                    item {
                        ArtistActionRow(
                            artistName = state.artist.name,
                            isFollowing = isSaved,
                            onToggleFollow = viewModel::toggleSave,
                            onShuffle = {
                                val mixTracks = state.mix?.tracks.orEmpty()
                                if (mixTracks.isNotEmpty()) {
                                    onPlayArtist(mixTracks.shuffled())
                                } else if (!isLoadingPlay) {
                                    viewModel.loadPlayQueue { tracks -> onPlayArtist(tracks.shuffled()) }
                                }
                            },
                            onPlay = {
                                if (state.topTracks.isNotEmpty() && !isLoadingPlay) {
                                    viewModel.loadPlayQueue { tracks -> onPlayArtist(tracks) }
                                }
                            },
                        )
                    }
                    if (state.topTracks.isNotEmpty()) {
                        item {
                            ArtistTop5Section(
                                artistName = state.artist.name,
                                tracks = state.topTracks,
                                onTrackClick = onTrackSelected,
                                onTrackLongClick = { selectedTrack = it },
                                modifier = Modifier.padding(top = 24.dp),
                            )
                        }
                    }
                    if (state.albums.isNotEmpty()) {
                        item {
                            ArtistDiscographySection(
                                albums = state.albums,
                                onAlbumClick = onAlbumSelected,
                                modifier = Modifier.padding(top = 32.dp),
                            )
                        }
                    }
                    if (state.mix != null || state.radio != null) {
                        item {
                            ArtistListenSection(
                                artist = state.artist,
                                mix = state.mix,
                                mixThumbnailUrl = state.mix?.thumbnailUrl.toHighRes(),
                                radio = state.radio,
                                onMixClick = {
                                    val mix = state.mix ?: return@ArtistListenSection
                                    val mixPlaylistId = mix.playlistId
                                    if (mixPlaylistId != null) {
                                        onMixPlaylistSelected(mixPlaylistId)
                                    } else if (mix.tracks.isNotEmpty()) {
                                        onPlayMixTrack(mix.tracks.first(), mix.tracks.drop(1))
                                    }
                                },
                                onRadioClick = {
                                    state.radio?.tracks?.let { tracks ->
                                        if (tracks.isNotEmpty()) onPlayMixTrack(tracks.first(), tracks.drop(1))
                                    }
                                },
                                modifier = Modifier.padding(top = 32.dp),
                            )
                        }
                    }
                    // Espacio para que el mini player y la barra no tapen la última sección.
                    item { Spacer(Modifier.height(120.dp)) }
                }
            }
        }

    }

    selectedTrack?.let { track ->
        TrackOptionsBottomSheet(
            isFavorite = track.id in favoriteTrackIds,
            isDownloaded = track.id in downloadedTrackIds,
            isDownloading = downloadingTrackIds.contains(track.id),
            onDownload = {
                trackActionsViewModel.downloadTrack(track)
                selectedTrack = null
            },
            onToggleFavorite = {
                trackActionsViewModel.toggleFavorite(track)
                selectedTrack = null
            },
            onAddToPlaylist = {
                AddToPlaylistController.pendingTrack = track
                selectedTrack = null
                onNavigateToAddToPlaylist()
            },
            onAddToQueue = {
                QueueController.addToQueue(track)
                selectedTrack = null
            },
            onDismiss = { selectedTrack = null }
        )
    }
}
