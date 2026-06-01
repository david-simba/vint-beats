package com.davidsimba.vintbeats.feature.library.ui.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.davidsimba.vintbeats.feature.library.domain.subtitle
import com.davidsimba.vintbeats.shared.components.CollectionAppBar
import com.davidsimba.vintbeats.shared.components.CollectionHeader
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.components.rememberScrollAppBarAlpha
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid

@Composable
fun DownloadsScreen(
    onBack: () -> Unit,
    onTrackClick: (Int) -> Unit,
    viewModel: DownloadsViewModel = hiltViewModel()
) {
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appBarAlpha = rememberScrollAppBarAlpha(lazyListState)

    val subtitle = if (downloads.isEmpty()) stringResource(R.string.downloads_empty_short)
                   else stringResource(R.string.downloads_count, downloads.size)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize().background(VintageBgDark)
        ) {
            item {
                CollectionHeader(
                    title = stringResource(R.string.downloads_title),
                    subtitle = subtitle,
                    imageUrl = null,
                    placeholderIcon = Icons.Rounded.Download
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(VintageBgDark)
                ) {
                    if (downloads.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.downloads_empty),
                                color = VintageGrayMid,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Spacer(Modifier.height(6.dp))
                        downloads.forEach { track ->
                            TrackCard(
                                title = track.trackTitle,
                                artist = track.subtitle(),
                                thumbnailUrl = track.trackThumbnailUrl,
                                onClick = { onTrackClick(track.id) }
                            )
                        }
                    }
                    Spacer(Modifier.height(100.dp))
                }
            }
        }

        CollectionAppBar(
            title = stringResource(R.string.downloads_title),
            alpha = appBarAlpha,
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopStart).zIndex(1f)
        )
    }
}
