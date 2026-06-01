package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DragIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.davidsimba.vintbeats.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.EqualizerBars
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import sh.calvin.reorderable.ReorderableColumn

@Composable
fun PlayerQueueSheet(
    currentTrack: Track?,
    queue: List<Track>,
    isPlaying: Boolean,
    onTrackClick: (Track) -> Unit,
    onReorder: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var localQueue by remember { mutableStateOf(queue) }
    val scrollState = rememberScrollState()

    LaunchedEffect(queue) {
        localQueue = queue
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.queue_up_next),
            color = VintageWhiteWarm,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        currentTrack?.let {
            QueueTrackRow(
                track = it,
                isCurrentTrack = true,
                isPlaying = isPlaying,
                modifier = Modifier,
                onClick = null
            )
        }

        if (localQueue.isEmpty()) {
            Text(
                text = stringResource(R.string.queue_loading),
                color = VintageWhite.copy(alpha = 0.4f),
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
            }
        }
    }
}

@Composable
private fun QueueTrackRow(
    track: Track,
    isCurrentTrack: Boolean,
    modifier: Modifier,
    onClick: (() -> Unit)?,
    isPlaying: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(top = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCurrentTrack) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.DragIndicator,
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

        TrackInfo(
            title = track.title,
            artist = track.artist,
            modifier = Modifier.weight(1f),
            titleWeight = if (isCurrentTrack) FontWeight.SemiBold else FontWeight.Medium,
            titleColor = if (isCurrentTrack) VintageWhiteWarm else VintageWhite
        )

        Spacer(Modifier.width(8.dp))

        if (isCurrentTrack) {
            EqualizerBars(
                isPlaying = isPlaying,
                color = VintageRedLight,
                maxHeight = 12.dp,
                barCount = 4,
                modifier = Modifier.width(24.dp)
            )
        } else {
            Text(
                text = track.durationText,
                color = VintageGrayMid,
                fontSize = 11.sp
            )
        }
    }
}
