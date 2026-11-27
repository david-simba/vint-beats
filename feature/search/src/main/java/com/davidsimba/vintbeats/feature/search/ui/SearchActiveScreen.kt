package com.davidsimba.vintbeats.feature.search.ui

import com.davidsimba.vintbeats.core.designsystem.R as DsR

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.search.domain.RecentSearch
import com.davidsimba.vintbeats.feature.search.ui.components.SearchInput
import com.davidsimba.vintbeats.feature.search.ui.components.SearchNoResults
import com.davidsimba.vintbeats.feature.search.ui.components.SearchResultRow
import com.davidsimba.vintbeats.feature.search.ui.components.SearchSectionTitle
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.core.designsystem.theme.TextoSecundario
import com.davidsimba.vintbeats.core.designsystem.components.track.TrackInfo
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBgDark
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayMid
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRedLight
import com.davidsimba.vintbeats.feature.search.R

@OptIn(ExperimentalFoundationApi::class)
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

    val subtitleArtist = stringResource(R.string.search_subtitle_artist)

    Box(modifier = Modifier.fillMaxSize().background(Fondo).statusBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
            stickyHeader {
                SearchInput(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    autoFocus = true,
                    modifier = Modifier
                        .background(Fondo)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    leading = {
                        IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(DsR.string.action_back),
                                tint = Fondo,
                                modifier = Modifier.size(20.dp)
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
                            .padding(start = 20.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.search_recent_title),
                            color = TextoPrincipal,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
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
                val openArtist: (Artist) -> Unit = { artist ->
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
                val openAlbum: (Album) -> Unit = { album ->
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
                val openTrack: (Track) -> Unit = { track ->
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

                if (state.tracks.isEmpty() && state.artists.isEmpty() && state.albums.isEmpty()) {
                    item { SearchNoResults(query = query) }
                } else {
                    // Mejor resultado: el primer artista; si no hay, la primera canción; si no, el primer álbum.
                    item(key = "mejor_resultado_titulo") {
                        SearchSectionTitle(stringResource(R.string.search_best_result))
                    }
                    item(key = "mejor_resultado") {
                        val artist = state.artists.firstOrNull()
                        val track = state.tracks.firstOrNull()
                        val album = state.albums.firstOrNull()
                        when {
                            artist != null -> SearchResultRow(
                                title = artist.name,
                                subtitle = subtitleArtist,
                                thumbnailUrl = artist.thumbnailUrl,
                                circular = true,
                                onClick = { openArtist(artist) },
                            )
                            track != null -> SearchResultRow(
                                title = track.title,
                                subtitle = stringResource(R.string.search_subtitle_song, track.artist),
                                thumbnailUrl = track.albumImageUrl,
                                circular = false,
                                onClick = { openTrack(track) },
                            )
                            album != null -> SearchResultRow(
                                title = album.title,
                                subtitle = albumSubtitle(album),
                                thumbnailUrl = album.thumbnailUrl,
                                circular = false,
                                onClick = { openAlbum(album) },
                            )
                        }
                    }
                    if (state.tracks.isNotEmpty()) {
                        item(key = "canciones_titulo") { SearchSectionTitle(stringResource(R.string.search_songs)) }
                        items(state.tracks, key = { "track_${it.id}" }) { track ->
                            SearchResultRow(
                                title = track.title,
                                subtitle = stringResource(R.string.search_subtitle_song, track.artist),
                                thumbnailUrl = track.albumImageUrl,
                                circular = false,
                                onClick = { openTrack(track) },
                            )
                        }
                    }
                    if (state.artists.isNotEmpty()) {
                        item(key = "artistas_titulo") { SearchSectionTitle(stringResource(R.string.search_artists)) }
                        items(state.artists.take(3), key = { "artist_${it.id}" }) { artist ->
                            SearchResultRow(
                                title = artist.name,
                                subtitle = subtitleArtist,
                                thumbnailUrl = artist.thumbnailUrl,
                                circular = true,
                                onClick = { openArtist(artist) },
                            )
                        }
                    }
                    if (state.albums.isNotEmpty()) {
                        item(key = "albumes_titulo") { SearchSectionTitle(stringResource(R.string.search_albums)) }
                        items(state.albums.take(3), key = { "album_${it.id}" }) { album ->
                            SearchResultRow(
                                title = album.title,
                                subtitle = albumSubtitle(album),
                                thumbnailUrl = album.thumbnailUrl,
                                circular = false,
                                onClick = { openAlbum(album) },
                            )
                        }
                    }
                }
            }
        }

        when (val state = uiState) {
            is SearchUiState.Loading -> CircularProgressIndicator(
                color = TextoSecundario,
                modifier = Modifier.align(Alignment.Center)
            )
            is SearchUiState.Error -> Text(
                text = state.message,
                color = TextoPrincipal,
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

// El modelo de álbum no trae el nombre del artista; se usa el año cuando existe.
@Composable
private fun albumSubtitle(album: Album): String =
    album.year?.takeIf { it.isNotBlank() }
        ?.let { stringResource(R.string.search_subtitle_album, it) }
        ?: stringResource(R.string.search_subtitle_album_plain)
