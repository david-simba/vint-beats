package com.davidsimba.vintbeats.feature.artist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun ArtistHeader(
    artist: Artist,
    hasTopTracks: Boolean,
    isLoadingPlay: Boolean,
    parallaxOffset: Float = 0f,
    onPlay: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clipToBounds()
    ) {
        AsyncImage(
            model = artist.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .graphicsLayer { translationY = parallaxOffset * 0.4f }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.0f),
                            0.45f to Color.Black.copy(alpha = 0.35f),
                            1.0f to VintageBgDark
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = artist.name,
                color = VintageWhite,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )

            if (hasTopTracks) {
                Spacer(Modifier.width(12.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .background(VintageRedLight, CircleShape)
                ) {
                    if (isLoadingPlay) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = VintageWhite,
                            strokeWidth = 2.dp,
                            strokeCap = StrokeCap.Round
                        )
                    } else {
                        IconButton(
                            onClick = onPlay,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = stringResource(R.string.artist_play),
                                tint = VintageWhite,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
