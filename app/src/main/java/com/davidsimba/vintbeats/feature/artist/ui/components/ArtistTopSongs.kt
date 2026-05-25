package com.davidsimba.vintbeats.feature.artist.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun ArtistTopSongsHeader() {
    Text(
        text = "Top Songs",
        color = VintageWhiteWarm,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun ArtistTopSongsEmpty() {
    Text(
        text = "No songs available.",
        color = VintageGrayMid,
        fontSize = 14.sp,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ArtistTopSongItem(track: Track, onClick: () -> Unit) {
    TrackCard(
        title = track.title,
        artist = track.artist,
        thumbnailUrl = track.albumImageUrl,
        onClick = onClick,
        imageSize = 42.dp
    )
}
