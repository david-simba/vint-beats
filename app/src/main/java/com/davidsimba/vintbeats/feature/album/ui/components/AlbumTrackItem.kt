package com.davidsimba.vintbeats.feature.album.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid

@Composable
fun AlbumTrackItem(index: Int, track: Track, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = index.toString(),
            color = VintageGrayMid,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(20.dp)
        )
        TrackInfo(
            title = track.title,
            artist = track.artist,
            modifier = Modifier.weight(1f),
            titleSize = 14.sp,
            artistSize = 12.sp
        )
        if (track.durationText.isNotBlank()) {
            Text(
                text = track.durationText,
                color = VintageGrayMid,
                fontSize = 12.sp
            )
        }
    }
}
