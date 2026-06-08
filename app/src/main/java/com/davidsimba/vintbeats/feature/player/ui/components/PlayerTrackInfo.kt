package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.components.VintClickableText
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun PlayerTrackInfo(
    modifier: Modifier = Modifier,
    title: String,
    artist: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onQueueOpen: (() -> Unit)? = null,
    onArtistClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = VintageWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            VintClickableText(
                text = artist,
                onClick = onArtistClick,
                color = VintageWhite.copy(alpha = if (onArtistClick != null) 1f else 0.7f),
                fontSize = 14.sp,
            )
        }
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) VintageRedLight else VintageWhite,
                modifier = Modifier.size(28.dp)
            )
        }
        if (onQueueOpen != null) {
            IconButton(
                onClick = onQueueOpen,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.PlaylistPlay,
                    contentDescription = "Queue",
                    tint = VintageWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
