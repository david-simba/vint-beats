package com.davidsimba.vintbeats.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.home.domain.ArtistRadioItem
import com.davidsimba.vintbeats.feature.home.ui.components.ArtistRadioSection
import com.davidsimba.vintbeats.feature.home.ui.components.ArtistRadioSkeleton
import com.davidsimba.vintbeats.feature.home.ui.components.HomeSectionSkeleton
import com.davidsimba.vintbeats.feature.home.ui.components.PlaylistSection
import com.davidsimba.vintbeats.feature.home.ui.components.QuickMixSection
import com.davidsimba.vintbeats.feature.home.ui.components.QuickMixSkeleton
import com.davidsimba.vintbeats.feature.home.ui.components.RecentAlbumsSection
import com.davidsimba.vintbeats.feature.home.ui.components.RecentlyPlayedSection
import com.davidsimba.vintbeats.shared.TrackActionsViewModel
import com.davidsimba.vintbeats.shared.components.TrackOptionsBottomSheet
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    playingTrackId: String? = null,
    isTrackPlaying: Boolean = false,
    onTrackSelected: (Track, List<Track>) -> Unit = { _, _ -> },
    onPlaylistSelected: (id: String, thumbnailUrl: String?, artistId: String?, artistName: String?) -> Unit = { _, _, _, _ -> },
    onAlbumSelected: (id: String) -> Unit = {},
    onRadioSelected: (ArtistRadioItem) -> Unit = {},
    onNavigateToOnboarding: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val needsOnboarding by viewModel.needsOnboarding.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val recentTracks by viewModel.recentTracks.collectAsStateWithLifecycle()
    val recentAlbums by viewModel.recentAlbums.collectAsStateWithLifecycle()
    val favoriteTrackIds by trackActionsViewModel.favoriteTrackIds.collectAsStateWithLifecycle()
    val downloadedTrackIds by trackActionsViewModel.downloadedTrackIds.collectAsStateWithLifecycle()
    val downloadingTrackId by trackActionsViewModel.downloadingTrackId.collectAsStateWithLifecycle()

    var selectedTrack by remember { mutableStateOf<Track?>(null) }

    LaunchedEffect(Unit) {
        val needsIt = viewModel.needsOnboarding.filterNotNull().first()
        if (needsIt && viewModel.tryConsumeOnboardingNavigation()) {
            onNavigateToOnboarding()
        }
    }
    LaunchedEffect(needsOnboarding) {
        if (needsOnboarding == false) viewModel.loadFeed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vintageBgGradient)
            .statusBarsPadding()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = if (userName.isNotBlank()) {
                                stringResource(R.string.home_greeting, userName)
                            } else {
                                stringResource(R.string.nav_home)
                            },
                            color = VintageWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    item { QuickMixSkeleton() }
                    item { HomeSectionSkeleton() }
                    item { ArtistRadioSkeleton() }
                    item { HomeSectionSkeleton() }
                }
            }

            is HomeUiState.Empty -> {
                Text(
                    text = stringResource(R.string.home_empty),
                    color = VintageGrayMid,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp)
                )
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = if (userName.isNotBlank()) {
                                stringResource(R.string.home_greeting, userName)
                            } else {
                                stringResource(R.string.nav_home)
                            },
                            color = VintageWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }

                    item {
                        if (state.quickMix.isNotEmpty()) {
                            QuickMixSection(
                                tracks = state.quickMix,
                                onTrackSelected = onTrackSelected,
                                onMenuClick = { selectedTrack = it },
                                playingTrackId = playingTrackId,
                                isTrackPlaying = isTrackPlaying,
                            )
                        } else {
                            QuickMixSkeleton()
                        }
                    }

                    state.sections.forEachIndexed { index, section ->
                        item(key = section.title) {
                            PlaylistSection(
                                section = section,
                                onPlaylistSelected = onPlaylistSelected
                            )
                        }
                        if (index == 0 && recentTracks.isNotEmpty()) {
                            item(key = "recientes") {
                                RecentlyPlayedSection(
                                    tracks = recentTracks,
                                    onTrackClick = { recent ->
                                        val asTrack = com.davidsimba.vintbeats.core.model.Track(
                                            id = recent.trackId,
                                            title = recent.title,
                                            artist = recent.artist,
                                            albumImageUrl = recent.thumbnailUrl,
                                            previewUrl = null,
                                            durationText = ""
                                        )
                                        onTrackSelected(asTrack, emptyList())
                                    }
                                )
                            }
                        }
                        if (index == 0 && recentAlbums.isNotEmpty()) {
                            item(key = "albumes_recientes") {
                                RecentAlbumsSection(
                                    albums = recentAlbums,
                                    onAlbumClick = { album -> onAlbumSelected(album.albumId) }
                                )
                            }
                        }
                        if (index == 0 && state.artistRadios.isNotEmpty()) {
                            item(key = "radio") {
                                ArtistRadioSection(
                                    radios = state.artistRadios,
                                    onRadioSelected = onRadioSelected,
                                )
                            }
                        }
                    }
                }
            }
        }
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
