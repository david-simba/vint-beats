package com.davidsimba.vintbeats.feature.search.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.davidsimba.vintbeats.feature.search.ui.components.CategorySheetContent
import com.davidsimba.vintbeats.feature.search.ui.components.SearchField
import com.davidsimba.vintbeats.feature.search.ui.components.exploreGrid
import com.davidsimba.vintbeats.shared.components.SectionLabel
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTrackSelected: (Track) -> Unit,
    onArtistSelected: (Artist) -> Unit,
    onAlbumSelected: (Album) -> Unit,
    onPlaylistSelected: (PlaylistSummary) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categorySheet by viewModel.categorySheet.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (categorySheet !is CategorySheetState.Hidden) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeCategory,
            sheetState = sheetState,
            containerColor = VintageBgDark
        ) {
            CategorySheetContent(
                state = categorySheet,
                onPlaylistSelected = { playlist ->
                    viewModel.closeCategory()
                    onPlaylistSelected(playlist)
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                SearchField(query = query, onQueryChange = viewModel::onQueryChange)
            }

            when (val state = uiState) {
                is SearchUiState.Idle -> {
                    exploreGrid(
                        categories = categories,
                        onCategoryClick = viewModel::openCategory
                    )
                }

                is SearchUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = com.davidsimba.vintbeats.shared.theme.VintageGrayCool
                            )
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
                        item { SectionLabel("Artists") }
                        items(state.artists.take(3)) { ArtistRow(it) { onArtistSelected(it) } }
                    }
                    if (state.albums.isNotEmpty()) {
                        item { SectionLabel("Albums") }
                        items(state.albums.take(3)) { AlbumRow(it) { onAlbumSelected(it) } }
                    }
                    if (state.tracks.isNotEmpty()) {
                        item { SectionLabel("Songs") }
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
