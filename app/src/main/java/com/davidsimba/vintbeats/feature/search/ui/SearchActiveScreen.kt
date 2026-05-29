package com.davidsimba.vintbeats.feature.search.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.PlaylistSummary
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.search.ui.components.AlbumRow
import com.davidsimba.vintbeats.feature.search.ui.components.ArtistRow
import com.davidsimba.vintbeats.feature.search.ui.components.SearchField
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayCool
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun SearchActiveScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    onArtistSelected: (Artist) -> Unit,
    onAlbumSelected: (Album) -> Unit,
    onPlaylistSelected: (PlaylistSummary) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
            stickyHeader {
                SearchField(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    autoFocus = true,
                    leadingIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = VintageBgDark
                            )
                        }
                    }
                )
            }

            when (val state = uiState) {
                is SearchUiState.Idle -> {}
                is SearchUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = VintageGrayCool)
                        }
                    }
                }
                is SearchUiState.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.message, color = VintageWhiteWarm, fontSize = 14.sp)
                        }
                    }
                }
                is SearchUiState.Success -> {
                    if (state.artists.isNotEmpty()) {
                        items(state.artists.take(3)) { artist ->
                            ArtistRow(artist) { onArtistSelected(artist) }
                        }
                    }
                    if (state.albums.isNotEmpty()) {
                        items(state.albums.take(3)) { album ->
                            AlbumRow(album) { onAlbumSelected(album) }
                        }
                    }
                    if (state.tracks.isNotEmpty()) {
                        items(state.tracks) { track ->
                            TrackCard(
                                title = track.title,
                                artist = track.artist,
                                thumbnailUrl = track.albumImageUrl,
                                onClick = { onTrackSelected(track) }
                            )
                        }
                    }
                }
            }
        }
    }
}
