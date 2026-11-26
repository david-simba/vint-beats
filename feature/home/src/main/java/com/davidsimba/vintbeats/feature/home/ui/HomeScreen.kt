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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.home.domain.ArtistRadioItem
import com.davidsimba.vintbeats.feature.home.domain.QuickAccessItem
import com.davidsimba.vintbeats.feature.home.ui.components.ArtistRadioSection
import com.davidsimba.vintbeats.feature.home.ui.components.ArtistsSection
import com.davidsimba.vintbeats.feature.home.ui.components.BestOfArtistsSection
import com.davidsimba.vintbeats.feature.home.ui.components.ArtistRadioSkeleton
import com.davidsimba.vintbeats.feature.home.ui.components.HomeHeader
import com.davidsimba.vintbeats.feature.home.ui.components.HomeSectionSkeleton
import com.davidsimba.vintbeats.feature.home.ui.components.HomeTrackOptionsSheet
import com.davidsimba.vintbeats.feature.home.ui.components.PlaylistSection
import com.davidsimba.vintbeats.feature.home.ui.components.QuickMixSection
import com.davidsimba.vintbeats.feature.home.ui.components.QuickMixSkeleton
import com.davidsimba.vintbeats.feature.home.ui.components.RecentAlbumsSection
import com.davidsimba.vintbeats.feature.home.ui.components.QuickAccessSection
import com.davidsimba.vintbeats.core.ui.TrackActionsViewModel
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayMid
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import com.davidsimba.vintbeats.feature.home.R

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
    onNavigateToAddToPlaylist: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onArtistClick: (artistId: String) -> Unit = {},
    onQuickMixClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val needsOnboarding by viewModel.needsOnboarding.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val quickAccess by viewModel.quickAccess.collectAsStateWithLifecycle()
    val recentAlbums by viewModel.recentAlbums.collectAsStateWithLifecycle()

    val openPlaylist: (String, String?, String?, String?) -> Unit = { id, thumbnailUrl, artistId, artistName ->
        viewModel.recordPlaylist(id, thumbnailUrl, artistId, artistName)
        onPlaylistSelected(id, thumbnailUrl, artistId, artistName)
    }

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
            .background(Fondo)
            .statusBarsPadding()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item { HomeHeader(userName = userName) }
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
                    item { HomeHeader(userName = userName) }

                    item(key = "accesos_rapidos") {
                        QuickAccessSection(
                            items = quickAccess,
                            onItemClick = { item ->
                                when (item) {
                                    is QuickAccessItem.Favorites -> onFavoritesClick()
                                    is QuickAccessItem.Album -> onAlbumSelected(item.albumId)
                                    is QuickAccessItem.Playlist ->
                                        openPlaylist(item.playlistId, item.thumbnailUrl, item.artistId, item.artistName)
                                    is QuickAccessItem.Track -> onTrackSelected(
                                        Track(
                                            id = item.trackId,
                                            title = item.title,
                                            artist = item.artist,
                                            albumImageUrl = item.thumbnailUrl,
                                            previewUrl = null,
                                            durationText = "",
                                        ),
                                        emptyList(),
                                    )
                                }
                            },
                        )
                    }

                    item {
                        if (state.quickMix.isNotEmpty()) {
                            QuickMixSection(
                                tracks = state.quickMix,
                                onTrackSelected = onTrackSelected,
                                onMenuClick = { selectedTrack = it },
                                onSeeAllClick = onQuickMixClick,
                            )
                        } else {
                            QuickMixSkeleton()
                        }
                    }

                    state.sections.forEachIndexed { index, section ->
                        item(key = section.title) {
                            if (section.isPrimary) {
                                BestOfArtistsSection(
                                    artists = section.playlists,
                                    onPlaylistSelected = openPlaylist,
                                )
                                ArtistsSection(
                                    artists = section.playlists,
                                    onArtistClick = onArtistClick,
                                )
                            } else {
                                PlaylistSection(
                                    section = section,
                                    onPlaylistSelected = openPlaylist
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
        HomeTrackOptionsSheet(
            track = track,
            playingTrackId = playingTrackId,
            onDismiss = { selectedTrack = null },
            onNavigateToAddToPlaylist = onNavigateToAddToPlaylist,
            trackActionsViewModel = trackActionsViewModel,
        )
    }
}
