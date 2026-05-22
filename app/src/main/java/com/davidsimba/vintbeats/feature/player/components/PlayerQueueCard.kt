package com.davidsimba.vintbeats.feature.player.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.feature.search.domain.Track
import com.davidsimba.vintbeats.shared.components.VintCard
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import sh.calvin.reorderable.ReorderableColumn

@Composable
fun PlayerQueueCard(
    currentTrack: Track?,
    queue: List<Track>,
    onTrackClick: (Track) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var localQueue by remember { mutableStateOf(queue) }

    LaunchedEffect(queue) {
        localQueue = queue
    }

    VintCard(modifier = modifier) {
        Text(
            text = "Queue",
            color = VintageWhiteWarm,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        HorizontalDivider(
            color = VintageGrayDeep.copy(alpha = 1f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        currentTrack?.let {
            QueueTrackRow(
                track = it,
                isCurrentTrack = true,
                modifier = Modifier,
                onClick = null
            )

        }

        if (localQueue.isEmpty()) {
            Text(
                text = "Loading up next tracks…",
                color = VintageWhitePure.copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        } else {
            ReorderableColumn(
                list = localQueue,
                onSettle = { from, to ->
                    localQueue = localQueue.toMutableList().apply { add(to, removeAt(from)) }
                    onReorder(from, to)
                },
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) { index, track, _ ->
                QueueTrackRow(
                    track = track,
                    isCurrentTrack = false,
                    modifier = Modifier.draggableHandle(),
                    onClick = { onTrackClick(track) }
                )
                HorizontalDivider(
                    color = VintageGrayDeep.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun QueueTrackRow(
    track: Track,
    isCurrentTrack: Boolean,
    modifier: Modifier,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(top = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCurrentTrack) Icons.Default.VolumeUp else Icons.Default.DragIndicator,
            contentDescription = null,
            tint = if (isCurrentTrack) VintageRedLight else VintageGrayMid,
            modifier = modifier
                .size(20.dp)
        )

        Spacer(Modifier.width(10.dp))

        AsyncImage(
            model = track.albumImageUrl,
            contentDescription = track.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = if (isCurrentTrack) VintageWhiteWarm else VintageWhitePure,
                fontSize = 13.sp,
                fontWeight = if (isCurrentTrack) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = track.artist,
                color = VintageGrayMid,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = track.durationText,
            color = VintageGrayMid,
            fontSize = 11.sp
        )
    }
}
