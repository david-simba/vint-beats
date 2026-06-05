package com.davidsimba.vintbeats.shared.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import com.davidsimba.vintbeats.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.shared.components.EqualizerBars
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun TrackCard(
    title: String,
    artist: String,
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    imageSize: Dp = 44.dp,
    isActive: Boolean = false,
    isPlaying: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(8.dp))
                .background(VintageGrayDeep)
        ) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            if (isActive) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                )
                if (isPlaying) {
                    EqualizerBars(
                        isPlaying = true,
                        barCount = 3,
                        maxHeight = (imageSize.value * 0.25f).dp,
                        color = VintageWhite.copy(0.75f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 4.dp)
                            .size(width = imageSize * 0.4f, height = imageSize * 0.25f)
                    )
                }
            }
        }

        TrackInfo(
            title = title,
            artist = "${stringResource(R.string.label_song)}  •  $artist",
            modifier = Modifier.weight(1f),
            titleSize = 14.sp,
            artistSize = 12.sp
        )

        trailingContent?.invoke()
    }
}
