package com.davidsimba.vintbeats.feature.library.ui.editplaylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.track.subtitle
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import sh.calvin.reorderable.ReorderableColumn

@Composable
fun EditPlaylistScreen(
    onBack: () -> Unit,
    viewModel: EditPlaylistViewModel = hiltViewModel()
) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    var localTracks by remember { mutableStateOf(tracks) }
    val scrollState = rememberScrollState()

    LaunchedEffect(tracks) { localTracks = tracks }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vintageBgGradient)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = VintageWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = stringResource(R.string.edit_playlist_title),
                color = VintageWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.verticalScroll(scrollState)) {
            ReorderableColumn(
                list = localTracks,
                onSettle = { from, to ->
                    localTracks = localTracks.toMutableList().apply { add(to, removeAt(from)) }
                    viewModel.reorderTracks(localTracks.map { it.id })
                },
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) { _, track, _ ->
                EditTrackRow(
                    track = track,
                    dragHandle = Modifier.draggableHandle(),
                    onRemove = { viewModel.removeTrack(track.id) }
                )
            }
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun EditTrackRow(
    dragHandle: Modifier,
    track: SavedTrack,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = null,
            tint = VintageGrayDeep,
            modifier = dragHandle.size(22.dp)
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VintageGrayDeep)
        ) {
            AsyncImage(
                model = track.trackThumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.trackTitle,
                color = VintageWhiteWarm,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = track.subtitle(),
                color = VintageGrayMid,
                fontSize = 12.sp,
                maxLines = 1
            )
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = null,
                tint = VintageRedLight,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
