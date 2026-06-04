package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun QuickMixSection(
    tracks: List<Track>,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onMenuClick: (Track) -> Unit,
) {
    val pages = tracks.chunked(4)
    val pagerState = rememberPagerState { pages.size }

    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = stringResource(R.string.home_seleccion_rapida),
            color = VintageWhiteWarm,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            beyondViewportPageCount = 1,
        ) { pageIndex ->
            Column(modifier = Modifier.fillMaxWidth()) {
                pages[pageIndex].forEachIndexed { indexInPage, track ->
                    val globalIndex = pageIndex * 4 + indexInPage
                    TrackCard(
                        title = track.title,
                        artist = track.artist,
                        thumbnailUrl = track.albumImageUrl,
                        imageSize = 54.dp,
                        onClick = { onTrackSelected(track, tracks.drop(globalIndex + 1)) },
                        trailingContent = {
                            IconButton(
                                onClick = { onMenuClick(track) },
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
        }
    }
}
