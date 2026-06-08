package com.davidsimba.vintbeats.shared.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material.icons.rounded.Queue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOptionsBottomSheet(
    isFavorite: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    isCurrentlyPlaying: Boolean = false,
    onDownload: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onAddToQueue: () -> Unit,
    onDismiss: () -> Unit
) {
    BottomSheet(onDismiss = onDismiss) {
        if (!isDownloaded) {
            BottomSheetMenuItem(
                label = stringResource(
                    if (isDownloading) R.string.player_option_downloading
                    else R.string.player_option_download
                ),
                icon = Icons.Rounded.Download,
                enabled = !isDownloading,
                onClick = onDownload
            )
        }
        BottomSheetMenuItem(
            label = stringResource(
                if (isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite
            ),
            icon = if (isFavorite) Icons.Rounded.HeartBroken else Icons.Rounded.FavoriteBorder,
            onClick = onToggleFavorite
        )
        BottomSheetMenuItem(
            label = stringResource(R.string.action_add_to_playlist),
            icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
            enabled = false,
            onClick = onAddToPlaylist
        )
        if (!isCurrentlyPlaying) {
            BottomSheetMenuItem(
                label = stringResource(R.string.action_add_to_queue),
                icon = Icons.Rounded.Queue,
                onClick = onAddToQueue
            )
        }
    }
}
