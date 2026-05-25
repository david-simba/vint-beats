package com.davidsimba.vintbeats.feature.artist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.feature.album.ui.components.AlbumCard

@Composable
fun ArtistAlbumsList(albums: List<Album>, onAlbumClick: (Album) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(albums) { album ->
            AlbumCard(album = album, onClick = { onAlbumClick(album) })
        }
    }
}
