package com.davidsimba.vintbeats.feature.library.ui.userplaylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.VintActionButton
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageOrangeLight

@Composable
fun UserPlaylistScreen(
    onBack: () -> Unit,
    onTrackClick: (Int) -> Unit,
    onPlayAll: (List<SavedTrack>) -> Unit,
    onAddSongsClick: () -> Unit,
    viewModel: UserPlaylistViewModel = hiltViewModel(),
) {
    val playlist by viewModel.playlist.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)

    val title = playlist?.name ?: ""
    val subtitle = when {
        playlist == null -> ""
        playlist!!.tracks.isEmpty() -> stringResource(R.string.playlist_empty_short)
        else -> stringResource(R.string.playlist_count, playlist!!.tracks.size)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .background(VintageBgDark),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
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
                        VintActionButton(
                            label = stringResource(R.string.action_add_songs),
                            icon = Icons.Rounded.Add,
                            onClick = onAddSongsClick,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        playlist!!.tracks.forEach { track ->
                            TrackCard(
                                title = track.trackTitle,
                                artist = track.subtitle(),
                                thumbnailUrl = track.trackThumbnailUrl,
                                onClick = { onTrackClick(track.id) },
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
            modifier = Modifier
                .align(Alignment.TopStart)
                .zIndex(1f),
        )
    }
}

