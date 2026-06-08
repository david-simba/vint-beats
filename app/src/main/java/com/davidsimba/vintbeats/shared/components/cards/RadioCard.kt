package com.davidsimba.vintbeats.shared.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun RadioCard(
    artistId: String,
    artistName: String,
    images: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = playlistAccentFor(artistId)
    val accentDeep = playlistAccentDeepFor(artistId)

    Box(
        modifier = modifier
            .width(180.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(listOf(accent, accentDeep)))
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(vertical = 12.dp)
                .padding(start = 6.dp, end = 12.dp)
        ) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                colorFilter = ColorFilter.tint(VintageWhite, BlendMode.SrcIn),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = stringResource(R.string.home_radio),
                color = VintageWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        val photos = images.take(3)

        Box(
            modifier = Modifier
                .width(175.dp)
                .height(90.dp)
                .align(Alignment.Center)
        ) {
            photos.getOrNull(0)?.let {
                AsyncImage(
                    model = it, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(75.dp).clip(CircleShape)
                        .align(Alignment.CenterStart).zIndex(1f)
                )
            }
            photos.getOrNull(1)?.let {
                AsyncImage(
                    model = it, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(90.dp).clip(CircleShape)
                        .align(Alignment.Center).zIndex(3f)
                )
            }
            photos.getOrNull(2)?.let {
                AsyncImage(
                    model = it, contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier.size(75.dp).clip(CircleShape)
                        .align(Alignment.CenterEnd).zIndex(1f)
                )
            }
        }

        Text(
            text = artistName,
            color = VintageWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 12.dp, start = 12.dp)
        )
    }
}
