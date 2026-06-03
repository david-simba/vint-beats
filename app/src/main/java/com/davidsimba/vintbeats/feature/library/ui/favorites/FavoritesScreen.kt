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
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HeartBroken
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
import com.davidsimba.vintbeats.feature.library.domain.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.subtitle
import com.davidsimba.vintbeats.shared.components.BottomSheet
import com.davidsimba.vintbeats.shared.components.BottomSheetMenuItem
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onTrackClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val downloadingTrackId by viewModel.downloadingTrackId.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)

    var selectedTrack by remember { mutableStateOf<SavedTrack?>(null) }

    val subtitle = if (favorites.isEmpty()) stringResource(R.string.favorites_empty_short)
                   else stringResource(R.string.favorites_count, favorites.size)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().background(VintageBgDark)
        ) {
            item {
                CollectionHeader(
                    title = stringResource(R.string.favorites_title),
                    subtitle = subtitle,
                    imageUrl = null,
                    placeholderIcon = Icons.Rounded.Favorite,
                    iconTint = VintageRedLight
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(VintageBgDark)
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

    selectedTrack?.let { track ->
        val isDownloading = downloadingTrackId == track.id
        BottomSheet(onDismiss = { selectedTrack = null }) {
            if (track.audioFilePath.isNullOrEmpty()) {
                BottomSheetMenuItem(
                    label = stringResource(
                        if (isDownloading) R.string.player_option_downloading
                        else R.string.player_option_download
                    ),
                    icon = Icons.Rounded.Download,
                    enabled = !isDownloading,
                    onClick = {
                        viewModel.downloadTrack(track)
                        selectedTrack = null
                    }
                )
            }
            BottomSheetMenuItem(
                label = stringResource(R.string.action_remove_favorite),
                icon = Icons.Rounded.HeartBroken,
                onClick = {
                    viewModel.removeFavorite(track.id)
                    selectedTrack = null
                }
            )
        }
    }
}
