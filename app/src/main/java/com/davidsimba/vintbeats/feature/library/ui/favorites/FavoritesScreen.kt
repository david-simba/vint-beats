package com.davidsimba.vintbeats.feature.library.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.track.subtitle
import com.davidsimba.vintbeats.feature.library.domain.track.toTrack
import com.davidsimba.vintbeats.shared.AddToPlaylistController
import com.davidsimba.vintbeats.shared.CollectionPlaybackState
import com.davidsimba.vintbeats.shared.QueueController
import com.davidsimba.vintbeats.shared.TrackActionsViewModel
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.TrackOptionsBottomSheet
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onTrackClick: (Int) -> Unit,
    onPlayAll: (List<SavedTrack>) -> Unit = {},
    onNavigateToAddToPlaylist: () -> Unit = {},
    playbackState: CollectionPlaybackState = CollectionPlaybackState(),
    viewModel: FavoritesViewModel = hiltViewModel(),
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val downloadedTrackIds by trackActionsViewModel.downloadedTrackIds.collectAsStateWithLifecycle()
    val downloadingTrackId by trackActionsViewModel.downloadingTrackId.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)

    var selectedTrack by remember { mutableStateOf<SavedTrack?>(null) }

    val subtitle = if (favorites.isEmpty()) stringResource(R.string.favorites_empty_short)
                   else stringResource(R.string.favorites_count, favorites.size)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().background(vintageBgGradient)
        ) {
            item {
                CollectionHeader(
                    title = stringResource(R.string.favorites_title),
                    subtitle = subtitle,
                    imageUrl = null,
                    placeholderIcon = Icons.Rounded.Favorite,
                    iconTint = VintageRedLight,
                    isPlaying = playbackState.isPlayingFrom(favorites.map { it.trackId }),
                    onPlayAll = if (favorites.isNotEmpty()) {
                        { onPlayAll(favorites) }
                    } else null
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    if (favorites.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.favorites_empty),
                                color = VintageGrayMid,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Spacer(Modifier.height(6.dp))
                        favorites.forEach { track ->
                            TrackCard(
                                title = track.trackTitle,
                                artist = track.subtitle(),
                                thumbnailUrl = track.trackThumbnailUrl,
                                isActive = playbackState.isActive(track.trackId),
                                isPlaying = playbackState.isPlaying(track.trackId),
                                onClick = { onTrackClick(track.id) },
                                trailingContent = {
                                    IconButton(
                                        onClick = { selectedTrack = track },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.MoreVert,
                                            contentDescription = null,
                                            tint = VintageGrayMid,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        CollectionAppBar(
            title = stringResource(R.string.favorites_title),
            alpha = appBarAlpha,
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart).zIndex(1f)
        )
    }

    selectedTrack?.let { savedTrack ->
        val track = savedTrack.toTrack()
        TrackOptionsBottomSheet(
            isFavorite = true,
            isDownloaded = !savedTrack.audioFilePath.isNullOrEmpty() ||
                savedTrack.trackId in downloadedTrackIds,
            isDownloading = downloadingTrackId == savedTrack.trackId,
            isCurrentlyPlaying = playbackState.isActive(savedTrack.trackId),
            onDownload = {
                trackActionsViewModel.downloadTrack(track)
                selectedTrack = null
            },
            onToggleFavorite = {
                trackActionsViewModel.toggleFavorite(track)
                selectedTrack = null
            },
            onAddToPlaylist = {
                AddToPlaylistController.pendingTrack = track
                selectedTrack = null
                onNavigateToAddToPlaylist()
            },
            onAddToQueue = {
                QueueController.addToQueue(track)
                selectedTrack = null
            },
            onDismiss = { selectedTrack = null }
        )
    }
}
