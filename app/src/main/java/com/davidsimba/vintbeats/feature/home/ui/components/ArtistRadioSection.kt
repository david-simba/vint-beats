package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.davidsimba.vintbeats.feature.home.domain.ArtistRadioItem
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

private const val CENTER_SIZE = 90
private const val SIDE_SIZE = 75

@Composable
fun ArtistRadioSection(
    radios: List<ArtistRadioItem>,
    onRadioSelected: (ArtistRadioItem) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            text = stringResource(R.string.home_radio),
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
            items(radios, key = { it.artistId }) { radio ->
                val artistLine = radio.tracks
                    .map { it.artist }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .joinToString(" · ")
                Column {
                    ArtistRadioCard(radio = radio, onClick = { onRadioSelected(radio) })
                    CardSubtitle(text = "Playlist · $artistLine")
                }
            }
        }
    }
}

@Composable
private fun ArtistRadioCard(
    radio: ArtistRadioItem,
    onClick: () -> Unit,
) {
    val accent = accentColorFor(radio.artistId)
    val accentDeep = accentDeepColorFor(radio.artistId)

    Box(
        modifier = Modifier
            .width(180.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Brush.verticalGradient(listOf(accent, accentDeep)))
            )
        }
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

        val images = radio.artistImages.take(3)
        val cardWidth = 175

        Box(
            modifier = Modifier
                .width(cardWidth.dp)
                .height(CENTER_SIZE.dp)
                .align(Alignment.Center)
        ) {
            images.getOrNull(0)?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(SIDE_SIZE.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterStart)
                        .zIndex(1f)
                )
            }
            images.getOrNull(1)?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(CENTER_SIZE.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .zIndex(3f)
                )
            }
            images.getOrNull(2)?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(SIDE_SIZE.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd)
                        .zIndex(1f)
                )
            }
        }

        Text(
            text = radio.artistName,
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
