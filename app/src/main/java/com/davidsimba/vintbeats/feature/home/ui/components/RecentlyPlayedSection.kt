package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.home.domain.RecentTrack
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

private val cardBottomGradient = Brush.verticalGradient(
    colorStops = arrayOf(0f to Color.Transparent, 0.5f to Color(0x99000000), 1f to Color(0xCC000000))
)

@Composable
fun RecentlyPlayedSection(
    tracks: List<RecentTrack>,
    onTrackClick: (RecentTrack) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            text = stringResource(R.string.home_recientes),
            color = VintageWhiteWarm,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tracks, key = { it.trackId }) { track ->
                Column {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VintageGrayDeep)
                            .clickable { onTrackClick(track) }
                    ) {
                        AsyncImage(
                            model = track.thumbnailUrl,
                            contentDescription = track.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(cardBottomGradient)
                        )
                        Text(
                            text = track.title,
                            color = VintageWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = track.artist,
                        color = VintageGrayMid,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }
    }
}
