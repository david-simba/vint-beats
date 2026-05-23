package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.feature.player.ui.PlayerState
import com.davidsimba.vintbeats.shared.components.background.Background
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun MiniPlayer(
    title: String,
    artist: String,
    thumbnailUrl: String?,
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying = playerState is PlayerState.Playing

    Box(modifier = modifier) {
        Background(thumbnailUrl = thumbnailUrl, horizontal = true, artColorsOnly = true, modifier = Modifier.matchParentSize())
        Box(modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.45f)))
        Column {
            HorizontalDivider(color = VintageGrayDeep.copy(alpha = 0.5f), thickness = 0.5.dp)
            TrackCard(
                title = title,
                artist = artist,
                thumbnailUrl = thumbnailUrl,
                imageSize = 46.dp,
                onClick = onTap,
                modifier = Modifier.fillMaxWidth(),
                trailingContent = {
                    IconButton(onClick = onTogglePlayPause) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = VintageWhitePure,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        }
    }
}
