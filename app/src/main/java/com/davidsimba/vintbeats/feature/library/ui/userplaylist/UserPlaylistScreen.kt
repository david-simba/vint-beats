package com.davidsimba.vintbeats.feature.library.ui.userplaylist

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.track.subtitle
import com.davidsimba.vintbeats.feature.library.domain.track.toTrack
import com.davidsimba.vintbeats.shared.TrackActionsViewModel
import com.davidsimba.vintbeats.shared.components.BottomSheet
import com.davidsimba.vintbeats.shared.components.BottomSheetMenuItem
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.TrackOptionsBottomSheet
import com.davidsimba.vintbeats.shared.components.VintActionButton
import com.davidsimba.vintbeats.shared.components.VintAlertDialog
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageOrangeLight
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPlaylistScreen(
    onBack: () -> Unit,
    onTrackClick: (Int) -> Unit,
    onPlayAll: (List<SavedTrack>) -> Unit,
    onAddSongsClick: () -> Unit,
    onEditClick: () -> Unit,
    onEditInfoClick: () -> Unit,
    playingTrackId: String? = null,
    isTrackPlaying: Boolean = false,
    viewModel: UserPlaylistViewModel = hiltViewModel(),
    trackActionsViewModel: TrackActionsViewModel = hiltViewModel()
) {
    val playlist by viewModel.playlist.collectAsStateWithLifecycle()
    val isDeleted by viewModel.isDeleted.collectAsStateWithLifecycle()
    val downloadedTrackIds by trackActionsViewModel.downloadedTrackIds.collectAsStateWithLifecycle()
    val downloadingTrackId by trackActionsViewModel.downloadingTrackId.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)

    var showOptionsSheet by remember { mutableStateOf(false) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    var selectedTrack by remember { mutableStateOf<SavedTrack?>(null) }

    LaunchedEffect(isDeleted) {
        if (isDeleted) onBack()
    }

    val title = playlist?.name ?: ""
    val subtitle = when {
        playlist == null -> ""
        playlist!!.tracks.isEmpty() -> stringResource(R.string.playlist_empty_short)
        else -> stringResource(R.string.playlist_count, playlist!!.tracks.size)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().background(vintageBgGradient),
        ) {
            item {
                val coverUri = playlist?.coverImagePath?.let { "file://$it" }
                CollectionHeader(
                    title = title,
                    subtitle = subtitle,
                    imageUrl = coverUri,
                    placeholderIcon = Icons.Rounded.LibraryMusic,
                    iconTint = VintageOrangeLight,
                    onPlayAll = if (playlist?.tracks?.isNotEmpty() == true) {
                        { onPlayAll(playlist!!.tracks) }
                    } else null,
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(VintageBgDark),
                ) {
                    if (playlist?.tracks.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MusicNote,
                                contentDescription = null,
                                tint = VintageGrayDeep,
                                modifier = Modifier.size(48.dp),
                            )
                            VintActionButton(
                                label = stringResource(R.string.action_add_songs),
                                icon = Icons.Rounded.Add,
                                onClick = onAddSongsClick,
                            )
                        }
                    } else {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VintActionButton(
                                label = stringResource(R.string.action_add_songs),
                                icon = Icons.Rounded.Add,
                                onClick = onAddSongsClick,
                            )
                            VintActionButton(
                                label = stringResource(R.string.action_edit_playlist),
                                icon = Icons.Rounded.Edit,
                                onClick = onEditClick,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        playlist!!.tracks.forEach { track ->
                            TrackCard(
                                title = track.trackTitle,
                                artist = track.subtitle(),
                                thumbnailUrl = track.trackThumbnailUrl,
                                isActive = track.trackId == playingTrackId,
                                isPlaying = track.trackId == playingTrackId && isTrackPlaying,
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
            title = title,
            alpha = appBarAlpha,
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart).zIndex(1f),
            trailingContent = {
                IconButton(onClick = { showOptionsSheet = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null,
                        tint = VintageWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        )
    }

    if (showOptionsSheet) {
        BottomSheet(onDismiss = { showOptionsSheet = false }) {
            BottomSheetMenuItem(
                label = stringResource(R.string.action_edit_info),
                icon = Icons.Rounded.Edit,
                onClick = {
                    showOptionsSheet = false
                    onEditInfoClick()
                }
            )
            BottomSheetMenuItem(
                label = stringResource(R.string.action_delete_playlist),
                icon = Icons.Rounded.DeleteOutline,
                onClick = {
                    showOptionsSheet = false
                    showDeleteAlert = true
                }
            )
        }
    }

    selectedTrack?.let { savedTrack ->
        val track = savedTrack.toTrack()
        TrackOptionsBottomSheet(
            isFavorite = savedTrack.isFavorite,
            isDownloaded = !savedTrack.audioFilePath.isNullOrEmpty() ||
                savedTrack.trackId in downloadedTrackIds,
            isDownloading = downloadingTrackId == savedTrack.trackId,
            onDownload = {
                trackActionsViewModel.downloadTrack(track)
                selectedTrack = null
            },
            onToggleFavorite = {
                trackActionsViewModel.toggleFavorite(track)
                selectedTrack = null
            },
            onAddToPlaylist = {},
            onDismiss = { selectedTrack = null }
        )
    }

    if (showDeleteAlert) {
        VintAlertDialog(
            title = stringResource(R.string.alert_delete_playlist_title),
            message = stringResource(R.string.alert_delete_playlist_message),
            confirmLabel = stringResource(R.string.action_delete),
            dismissLabel = stringResource(R.string.action_cancel),
            confirmColor = VintageRedLight,
            onConfirm = {
                showDeleteAlert = false
                viewModel.deletePlaylist()
            },
            onDismiss = { showDeleteAlert = false }
        )
    }
}
