package com.davidsimba.vintbeats.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.ExploreCategory
import com.davidsimba.vintbeats.core.model.PlaylistSummary
import com.davidsimba.vintbeats.feature.search.ui.CategorySheetState
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageBlueLight
import com.davidsimba.vintbeats.shared.theme.VintageBrownLight
import com.davidsimba.vintbeats.shared.theme.VintageGrayCool
import com.davidsimba.vintbeats.shared.theme.VintageGreenLight
import com.davidsimba.vintbeats.shared.theme.VintageOrangeLight
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageTealLight
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import com.davidsimba.vintbeats.shared.theme.VintageYellowLight

private val categoryColors = listOf(
    VintageRedLight, VintageOrangeLight, VintageYellowLight,
    VintageGreenLight, VintageTealLight, VintageBlueLight,
    VintageBrownLight, VintageTealLight, VintageRedLight, VintageGreenLight
)

fun LazyListScope.exploreGrid(
    categories: List<ExploreCategory>,
    onCategoryClick: (ExploreCategory) -> Unit
) {
    if (categories.isEmpty()) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VintageGrayCool, modifier = Modifier.size(24.dp))
            }
        }
        return
    }

    item {
        Text(
            text = "Explore",
            color = VintageWhiteWarm,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    val rows = categories.chunked(2)
    items(rows) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            row.forEach { category ->
                CategoryCard(
                    title = category.title,
                    color = categoryColors[categories.indexOf(category) % categoryColors.size],
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick(category) }
                )
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(2f)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            color = VintageWhitePure,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun CategorySheetContent(
    state: CategorySheetState,
    onPlaylistSelected: (PlaylistSummary) -> Unit
) {
    when (state) {
        is CategorySheetState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VintageGrayCool)
            }
        }
        is CategorySheetState.Error -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Couldn't load playlists", color = VintageGrayCool, fontSize = 14.sp)
            }
        }
        is CategorySheetState.Success -> {
            val result = state.result
            LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
                item {
                    Text(
                        text = result.title,
                        color = VintageWhitePure,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
                items(result.playlists) { playlist ->
                    PlaylistRow(playlist = playlist, onClick = { onPlaylistSelected(playlist) })
                }
            }
        }
        is CategorySheetState.Hidden -> {}
    }
}

@Composable
fun PlaylistRow(playlist: PlaylistSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VintageBgDark)
        )
        Spacer(Modifier.width(14.dp))
        TrackInfo(
            title = playlist.title,
            artist = playlist.subtitle,
            titleSize = 14.sp,
            titleWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ArtistRow(artist: Artist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = artist.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(VintageBgDark)
        )
        Spacer(Modifier.width(14.dp))
        TrackInfo(
            title = artist.name,
            artist = "Artist",
            titleSize = 15.sp,
            titleWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AlbumRow(album: Album, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = album.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VintageBgDark)
        )
        Spacer(Modifier.width(14.dp))
        TrackInfo(
            title = album.title,
            artist = buildString {
                append("Album")
                if (!album.year.isNullOrBlank()) append("  •  ${album.year}")
            },
            titleSize = 15.sp,
            titleWeight = FontWeight.SemiBold
        )
    }
}
