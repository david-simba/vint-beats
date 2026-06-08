package com.davidsimba.vintbeats.feature.search.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.search.domain.RecentSearch
import com.davidsimba.vintbeats.feature.search.ui.components.AlbumRow
import com.davidsimba.vintbeats.feature.search.ui.components.ArtistRow
import com.davidsimba.vintbeats.feature.search.ui.components.SearchField
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun SearchActiveScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track) -> Unit,
    onArtistSelected: (Artist) -> Unit,
    onAlbumSelected: (Album) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val showRecents = query.isEmpty() && uiState is SearchUiState.Idle && recentSearches.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 28.dp)) {
            stickyHeader {
                SearchField(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    autoFocus = true,
                    leadingIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                                tint = VintageBgDark
                            )
                        }
                    }
                )
            }

            if (showRecents) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.search_recent_title),
                            color = VintageWhiteWarm,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::clearHistory) {
                            Text(
                                text = stringResource(R.string.search_clear_history),
                                color = VintageRedLight,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }

                items(recentSearches, key = { "${it.type}_${it.id}" }) { item ->
                    RecentSearchRow(
                        item = item,
                        onRemove = { viewModel.removeRecentSearch(item) },
                        onClick = {
                            focusManager.clearFocus()
                            when (item.type) {
                                RecentSearch.Type.TRACK -> onTrackSelected(item.toTrack())
                                RecentSearch.Type.ALBUM -> onAlbumSelected(item.toAlbum())
                                RecentSearch.Type.ARTIST -> onArtistSelected(item.toArtist())
                            }
                        }
                    )
                }
            }

            if (uiState is SearchUiState.Success) {
                val state = uiState as SearchUiState.Success
                if (state.artists.isNotEmpty()) {
                    items(state.artists.take(3)) { artist ->
                        ArtistRow(artist) {
                            focusManager.clearFocus()
                            viewModel.saveRecentSearch(
                                RecentSearch(
                                    id = artist.id,
                                    title = artist.name,
                                    subtitle = artist.subtitle,
                                    thumbnailUrl = artist.thumbnailUrl,
                                    type = RecentSearch.Type.ARTIST,
                                )
                            )
                            onArtistSelected(artist)
                        }
                    }
                }
                if (state.albums.isNotEmpty()) {
                    items(state.albums.take(3)) { album ->
                        AlbumRow(album) {
                            focusManager.clearFocus()
                            viewModel.saveRecentSearch(
                                RecentSearch(
                                    id = album.id,
                                    title = album.title,
                                    subtitle = album.year,
                                    thumbnailUrl = album.thumbnailUrl,
                                    type = RecentSearch.Type.ALBUM,
                                )
                            )
                            onAlbumSelected(album)
                        }
                    }
                }
                if (state.tracks.isNotEmpty()) {
                    items(state.tracks) { track ->
                        TrackCard(
                            title = track.title,
                            artist = track.artist,
                            thumbnailUrl = track.albumImageUrl,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.saveRecentSearch(
                                    RecentSearch(
                                        id = track.id,
                                        title = track.title,
                                        subtitle = track.artist,
                                        thumbnailUrl = track.albumImageUrl,
                                        type = RecentSearch.Type.TRACK,
                                    )
                                )
                                onTrackSelected(track)
                            }
                        )
                    }
                }
            }
        }

        when (val state = uiState) {
            is SearchUiState.Loading -> CircularProgressIndicator(
                color = VintageGray,
                modifier = Modifier.align(Alignment.Center)
            )
            is SearchUiState.Error -> Text(
                text = state.message,
                color = VintageWhiteWarm,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            else -> {}
        }
    }
}

@Composable
private fun RecentSearchRow(
    item: RecentSearch,
    onRemove: () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val thumbnailShape = if (item.type == RecentSearch.Type.ARTIST) CircleShape
                             else RoundedCornerShape(6.dp)
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(thumbnailShape)
                .background(VintageBgDark)
        )
        Spacer(Modifier.width(14.dp))
        TrackInfo(
            title = item.title,
            artist = item.subtitle ?: "",
            titleSize = 14.sp,
            titleWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = VintageGrayMid,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
