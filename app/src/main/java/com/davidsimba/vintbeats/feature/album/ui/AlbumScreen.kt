package com.davidsimba.vintbeats.feature.album.ui

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
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.davidsimba.vintbeats.feature.album.ui.components.AlbumTrackItem
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun AlbumScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onPlayAlbum: (List<Track>) -> Unit,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)
    val albumTitle = (uiState as? AlbumUiState.Success)?.album?.title.orEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is AlbumUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VintageGray
                )
            }

            is AlbumUiState.Error -> {
                Text(
                    text = state.message,
                    color = VintageWhite,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
            }

            is AlbumUiState.Success -> {
                LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize().background(VintageBgDark)) {
                    item {
                        CollectionHeader(
                            title = state.album.title,
                            subtitle = buildString {
                                append(state.album.artist)
                                if (!state.album.year.isNullOrBlank()) append("  •  ${state.album.year}")
                            }.ifBlank { null },
                            imageUrl = state.album.thumbnailUrl,
                            placeholderIcon = Icons.Rounded.Album,
                            onPlayAll = if (state.album.tracks.isNotEmpty()) {
                                { onPlayAlbum(state.album.tracks) }
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
                            if (state.album.tracks.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                state.album.tracks.forEachIndexed { index, track ->
                                    AlbumTrackItem(
                                        index = index + 1,
                                        track = track,
                                        onClick = { onTrackSelected(track, state.album.tracks.drop(index + 1)) }
                                    )
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.album_no_tracks),
                                    color = VintageGrayMid,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        CollectionAppBar(
            title = albumTitle,
            alpha = appBarAlpha,
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart).zIndex(1f),
            trailingContent = {
                if (uiState is AlbumUiState.Success) {
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
}
