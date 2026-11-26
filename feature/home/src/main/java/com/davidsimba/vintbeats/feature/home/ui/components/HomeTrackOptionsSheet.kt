package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.common.QueueController
import com.davidsimba.vintbeats.core.designsystem.components.track.TrackOptionsBottomSheet
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.ui.AddToPlaylistController
import com.davidsimba.vintbeats.core.ui.TrackActionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTrackOptionsSheet(
    track: Track,
    playingTrackId: String?,
    onDismiss: () -> Unit,
    onNavigateToAddToPlaylist: () -> Unit,
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel(),
) {
    val favoriteTrackIds = trackActionsViewModel.favoriteTrackIds.collectAsStateWithLifecycle().value
    val downloadedTrackIds = trackActionsViewModel.downloadedTrackIds.collectAsStateWithLifecycle().value
    val downloadingTrackIds = trackActionsViewModel.downloadingTrackIds.collectAsStateWithLifecycle().value

    TrackOptionsBottomSheet(
        isFavorite = track.id in favoriteTrackIds,
        isDownloaded = track.id in downloadedTrackIds,
        isDownloading = downloadingTrackIds.contains(track.id),
        isCurrentlyPlaying = track.id == playingTrackId,
        onDownload = {
            trackActionsViewModel.downloadTrack(track)
            onDismiss()
        },
        onToggleFavorite = {
            trackActionsViewModel.toggleFavorite(track)
            onDismiss()
        },
        onAddToPlaylist = {
            AddToPlaylistController.pendingTrack = track
            onDismiss()
            onNavigateToAddToPlaylist()
        },
        onAddToQueue = {
            QueueController.addToQueue(track)
            onDismiss()
        },
        onDismiss = onDismiss,
    )
}
