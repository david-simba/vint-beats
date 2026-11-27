package com.davidsimba.vintbeats.feature.library.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.designsystem.components.layout.ImageShape
import com.davidsimba.vintbeats.core.designsystem.R as DsR
import com.davidsimba.vintbeats.feature.library.R
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryCardColor
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryCardGrid
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryCardList
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryEmptyState
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryFilterPill
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryItem
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryPinnedCards
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryPrimary
import com.davidsimba.vintbeats.feature.library.ui.components.LibrarySecondary

@Composable
fun LibraryScreen(
    onFavoritesClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    onPlaylistClick: (Int) -> Unit = {},
    onCreatePlaylistClick: () -> Unit = {},
    onAlbumClick: (String) -> Unit = {},
    onArtistClick: (String) -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val favoritesCount by viewModel.favoritesCount.collectAsStateWithLifecycle()
    val downloadsCount by viewModel.downloadsCount.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val savedAlbums by viewModel.savedAlbums.collectAsStateWithLifecycle()
    val savedArtists by viewModel.savedArtists.collectAsStateWithLifecycle()
    val isGrid by viewModel.isGridView.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    val playlistItems = playlists.map { playlist ->
        LibraryItem(
            icon = Icons.Rounded.LibraryMusic,
            title = playlist.name,
            subtitle = stringResource(
                if (playlist.trackCount == 1) R.string.library_subtitle_playlist_one
                else R.string.library_subtitle_playlist_many,
                playlist.trackCount,
            ),
            onClick = { onPlaylistClick(playlist.id) },
            imageUrl = playlist.coverImagePath?.let { "file://$it" },
        )
    }
    val albumItems = savedAlbums.map { album ->
        LibraryItem(
            icon = Icons.Rounded.Album,
            title = album.title,
            subtitle = stringResource(R.string.library_subtitle_album, album.artist),
            onClick = { onAlbumClick(album.albumId) },
            imageUrl = album.thumbnailUrl,
            imageShape = ImageShape.Square,
        )
    }
    val artistItems = savedArtists.map { artist ->
        LibraryItem(
            icon = Icons.Rounded.Person,
            title = artist.name,
            subtitle = stringResource(DsR.string.label_artist),
            onClick = { onArtistClick(artist.artistId) },
            imageUrl = artist.thumbnailUrl,
            imageShape = ImageShape.Circle,
        )
    }
    val visibleItems = when (filter) {
        LibraryFilter.Playlists -> playlistItems
        LibraryFilter.Albums -> albumItems
        LibraryFilter.Artists -> artistItems
        null -> playlistItems + albumItems + artistItems
    }

    val likedTitle = stringResource(R.string.library_liked_title)
    val likedSubtitle = stringResource(
        if (favoritesCount == 1) R.string.library_songs_one else R.string.library_songs_many,
        favoritesCount,
    )
    val downloadsTitle = stringResource(DsR.string.downloads_title)
    val downloadsSubtitle = stringResource(
        if (downloadsCount == 1) R.string.library_downloads_one else R.string.library_downloads_many,
        downloadsCount,
    )

    val pinned: @Composable () -> Unit = {
        LibraryPinnedCards(
            likedTitle = likedTitle,
            likedSubtitle = likedSubtitle,
            downloadsTitle = downloadsTitle,
            downloadsSubtitle = downloadsSubtitle,
            onLikedClick = onFavoritesClick,
            onDownloadsClick = onDownloadsClick,
        )
    }
    val recentLabel: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Sort,
                contentDescription = null,
                tint = LibrarySecondary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.library_recent),
                color = LibrarySecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
    val emptyState: @Composable () -> Unit = {
        val (title, message) = when (filter) {
            LibraryFilter.Playlists ->
                stringResource(R.string.library_empty_playlists_title) to stringResource(R.string.library_empty_playlists_text)
            LibraryFilter.Albums ->
                stringResource(R.string.library_empty_albums_title) to stringResource(R.string.library_empty_albums_text)
            LibraryFilter.Artists ->
                stringResource(R.string.library_empty_artists_title) to stringResource(R.string.library_empty_artists_text)
            null ->
                stringResource(R.string.library_empty_all_title) to stringResource(R.string.library_empty_all_text)
        }
        val canCreate = filter == null || filter == LibraryFilter.Playlists
        LibraryEmptyState(
            title = title,
            message = message,
            actionLabel = if (canCreate) stringResource(R.string.library_create_playlist) else null,
            onAction = onCreatePlaylistClick,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E10))
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.library_title),
                color = LibraryPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button) { viewModel.toggleGridView() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isGrid) Icons.AutoMirrored.Outlined.ViewList else Icons.Outlined.GridView,
                    contentDescription = stringResource(
                        if (isGrid) R.string.library_view_list else R.string.library_view_grid
                    ),
                    tint = Color(0xFFD4D4D8),
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(LibraryCardColor)
                    .clickable(role = Role.Button, onClick = onCreatePlaylistClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.library_create_playlist),
                    tint = LibraryPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LibraryFilter.entries.forEach { item ->
                LibraryFilterPill(
                    label = stringResource(
                        when (item) {
                            LibraryFilter.Playlists -> R.string.library_filter_playlists
                            LibraryFilter.Albums -> R.string.library_filter_albums
                            LibraryFilter.Artists -> R.string.library_filter_artists
                        }
                    ),
                    selected = filter == item,
                    onClick = { viewModel.setFilter(item) },
                )
            }
        }

        val bottomPadding = 120.dp
        if (isGrid) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = bottomPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) { pinned() }
                if (visibleItems.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) { emptyState() }
                } else {
                    item(span = { GridItemSpan(maxLineSpan) }) { recentLabel() }
                    items(visibleItems.size) { index ->
                        val item = visibleItems[index]
                        LibraryCardGrid(
                            icon = item.icon,
                            title = item.title,
                            subtitle = item.subtitle,
                            onClick = item.onClick,
                            imageUrl = item.imageUrl,
                            imageShape = item.imageShape,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 20.dp, bottom = bottomPadding),
            ) {
                item { Box(Modifier.padding(horizontal = 20.dp)) { pinned() } }
                if (visibleItems.isEmpty()) {
                    item { Box(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) { emptyState() } }
                } else {
                    item { Box(Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)) { recentLabel() } }
                    items(visibleItems) { item ->
                        LibraryCardList(
                            icon = item.icon,
                            title = item.title,
                            subtitle = item.subtitle,
                            onClick = item.onClick,
                            imageUrl = item.imageUrl,
                            imageShape = item.imageShape,
                        )
                    }
                }
            }
        }
    }
}
