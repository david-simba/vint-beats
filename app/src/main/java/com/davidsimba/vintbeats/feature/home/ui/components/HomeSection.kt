package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.background
import kotlin.math.absoluteValue
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.feature.home.domain.HomeSectionPlaylists
import com.davidsimba.vintbeats.feature.home.domain.PlaylistItem
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageBlue
import com.davidsimba.vintbeats.shared.theme.VintageBlueDeep
import com.davidsimba.vintbeats.shared.theme.VintageGreen
import com.davidsimba.vintbeats.shared.theme.VintageGreenDeep
import com.davidsimba.vintbeats.shared.theme.VintageOrange
import com.davidsimba.vintbeats.shared.theme.VintageOrangeDeep
import com.davidsimba.vintbeats.shared.theme.VintageRed
import com.davidsimba.vintbeats.shared.theme.VintageRedDeep
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun HomeSection(
    section: HomeSectionPlaylists,
    onPlaylistSelected: (id: String, thumbnailUrl: String?, artistId: String?, artistName: String?) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            text = section.title,
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
            items(section.playlists, key = { it.id }) { playlist ->
                PlaylistCard(
                    playlist = playlist,
                    showStripe = !section.title.lowercase().contains("descubre"),
                    onClick = {
                        onPlaylistSelected(
                            playlist.id,
                            playlist.thumbnailUrl,
                            playlist.artistId,
                            playlist.artistName
                        )
                    }
                )
            }
        }
    }
}

private val accentColors = listOf(
    VintageRed,
    VintageOrange,
    VintageGreen,
    VintageBlue,
)

private fun accentColorFor(id: String) = accentColors[id.hashCode().absoluteValue % accentColors.size]

@Composable
fun PlaylistCard(
    playlist: PlaylistItem,
    showStripe: Boolean = true,
    onClick: () -> Unit,
) {
    if (!showStripe) {
        AsyncImage(
            model = playlist.thumbnailUrl,
            contentDescription = playlist.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(175.dp)
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
        )
        return
    }

    val accent = accentColorFor(playlist.id)
    Box(
        modifier = Modifier
            .width(175.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.matchParentSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(accent))
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(VintageWhite))
        }

        Text(
            text = "This is",
            color = VintageWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        )

        AsyncImage(
            model = playlist.thumbnailUrl,
            contentDescription = playlist.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(6.dp))
                .align(Alignment.Center)
        )

        Text(
            text = playlist.artistName ?: playlist.title,
            color = VintageBgDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
        )
    }
}
