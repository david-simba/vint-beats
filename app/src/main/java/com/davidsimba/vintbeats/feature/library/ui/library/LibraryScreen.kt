package com.davidsimba.vintbeats.feature.library.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryCardGrid
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryCardList
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryItem
import com.davidsimba.vintbeats.shared.components.ImageShape
import com.davidsimba.vintbeats.shared.components.VintFilterChip
import com.davidsimba.vintbeats.shared.theme.VintageBlueLight
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageOrangeLight
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageTealLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

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

    val userItems = listOf(
        LibraryItem(
            icon = Icons.Rounded.Favorite,
            iconTint = VintageRedLight,
            iconBg = VintageRedLight.copy(alpha = 0.15f),
            title = stringResource(R.string.favorites_title),
            subtitle = if (favoritesCount == 0) stringResource(R.string.favorites_empty_short)
                       else stringResource(R.string.favorites_count, favoritesCount),
            onClick = onFavoritesClick,
        ),
        LibraryItem(
            icon = Icons.Rounded.Download,
            iconTint = VintageWhite,
            iconBg = VintageWhite.copy(alpha = 0.1f),
            title = stringResource(R.string.downloads_title),
            subtitle = if (downloadsCount == 0) stringResource(R.string.downloads_empty_short)
                       else stringResource(R.string.downloads_count, downloadsCount),
            onClick = onDownloadsClick,
        ),
    ) + playlists.map { playlist ->
        LibraryItem(
            icon = Icons.Rounded.LibraryMusic,
            iconTint = VintageOrangeLight,
            iconBg = VintageOrangeLight.copy(alpha = 0.15f),
            title = playlist.name,
            subtitle = if (playlist.trackCount == 0) stringResource(R.string.playlist_empty_short)
                       else stringResource(R.string.playlist_count, playlist.trackCount),
            onClick = { onPlaylistClick(playlist.id) },
            imageUrl = playlist.coverImagePath?.let { "file://$it" },
        )
    }

    val albumItems = savedAlbums.map { album ->
        LibraryItem(
            icon = Icons.Rounded.Album,
            iconTint = VintageBlueLight,
            iconBg = VintageBlueLight.copy(alpha = 0.15f),
            title = album.title,
            subtitle = album.artist,
            onClick = { onAlbumClick(album.albumId) },
            imageUrl = album.thumbnailUrl,
            imageShape = ImageShape.Square,
        )
    }

    val artistItems = savedArtists.map { artist ->
        LibraryItem(
            icon = Icons.Rounded.Person,
            iconTint = VintageTealLight,
            iconBg = VintageTealLight.copy(alpha = 0.15f),
            title = artist.name,
            subtitle = stringResource(R.string.label_artist),
            onClick = { onArtistClick(artist.artistId) },
            imageUrl = artist.thumbnailUrl,
            imageShape = ImageShape.Circle,
        )
    }

    val visibleItems = when (filter) {
        LibraryFilter.User -> userItems
        LibraryFilter.Albums -> albumItems
        LibraryFilter.Artists -> artistItems
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.library_title),
                color = VintageWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { viewModel.toggleGridView() }) {
                Icon(
                    imageVector = if (isGrid) Icons.AutoMirrored.Rounded.ViewList else Icons.Rounded.GridView,
                    contentDescription = null,
                    tint = VintageWhite.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp),
                )
            }
            IconButton(onClick = onCreatePlaylistClick) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.create_playlist_title),
                    tint = VintageWhite.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(LibraryFilter.entries) { item ->
                VintFilterChip(
                    label = when (item) {
                        LibraryFilter.User -> stringResource(R.string.library_filter_yours)
                        LibraryFilter.Albums -> stringResource(R.string.library_filter_albums)
                        LibraryFilter.Artists -> stringResource(R.string.library_filter_artists)
                    },
                    selected = filter == item,
                    onClick = { viewModel.setFilter(item) },
                )
            }
        }

        if (visibleItems.isEmpty()) {
            val emptyText = when (filter) {
                LibraryFilter.Albums -> stringResource(R.string.library_filter_albums_empty)
                LibraryFilter.Artists -> stringResource(R.string.library_filter_artists_empty)
                LibraryFilter.User -> stringResource(R.string.library_empty)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emptyText, color = VintageGrayMid, fontSize = 14.sp)
            }
        } else if (isGrid) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(visibleItems.size) { index ->
                    val item = visibleItems[index]
                    LibraryCardGrid(
                        icon = item.icon,
                        iconTint = item.iconTint,
                        iconBg = item.iconBg,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = item.onClick,
                        imageUrl = item.imageUrl,
                        imageShape = item.imageShape,
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(visibleItems.size) { index ->
                    val item = visibleItems[index]
                    LibraryCardList(
                        icon = item.icon,
                        iconTint = item.iconTint,
                        iconBg = item.iconBg,
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
