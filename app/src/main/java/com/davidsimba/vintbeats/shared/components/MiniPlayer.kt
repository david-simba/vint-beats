package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette
import com.davidsimba.vintbeats.feature.player.ui.PlayerState
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun MiniPlayer(
    cassette: SavedCassette,
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    MiniPlayerContent(
        title = cassette.trackTitle,
        artist = cassette.trackArtist,
        thumbnailUrl = cassette.trackThumbnailUrl,
        playerState = playerState,
        onTogglePlayPause = onTogglePlayPause,
        onTap = onTap,
        modifier = modifier
    )
}

@Composable
fun MiniPlayerTrack(
    trackTitle: String,
    trackArtist: String,
    thumbnailUrl: String?,
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    MiniPlayerContent(
        title = trackTitle,
        artist = trackArtist,
        thumbnailUrl = thumbnailUrl,
        playerState = playerState,
        onTogglePlayPause = onTogglePlayPause,
        onTap = onTap,
        modifier = modifier
    )
}

@Composable
private fun MiniPlayerContent(
    title: String,
    artist: String,
    thumbnailUrl: String?,
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying = playerState is PlayerState.Playing

    Column(modifier = modifier) {
        HorizontalDivider(color = VintageGrayDeep.copy(alpha = 0.5f), thickness = 0.5.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VintageBgDark)
                .clickable(onClick = onTap)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(VintageGrayDeep)
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = VintageWhitePure,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = artist,
                    color = VintageGrayMid,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onTogglePlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = VintageWhitePure,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
