package com.davidsimba.vintbeats.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.material3.CircularProgressIndicator
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.components.cards.CategoryCard
import com.davidsimba.vintbeats.shared.components.cards.CategoryCardSkeleton
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageBlue
import com.davidsimba.vintbeats.shared.theme.VintageBrownLight
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageGreen
import com.davidsimba.vintbeats.shared.theme.VintageOrange
import com.davidsimba.vintbeats.shared.theme.VintageRed
import com.davidsimba.vintbeats.shared.theme.VintageTeal
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageYellow

private val categoryColors = listOf(
    VintageRed, VintageOrange, VintageYellow,
    VintageGreen, VintageTeal, VintageBlue,
    VintageBrownLight, VintageTeal, VintageRed, VintageGreen
)

fun LazyListScope.exploreGrid(
    categories: List<ExploreCategory>,
    onCategoryClick: (ExploreCategory) -> Unit
) {
    if (categories.isEmpty()) {
        items(3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp, top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryCardSkeleton(modifier = Modifier.weight(1f))
                CategoryCardSkeleton(modifier = Modifier.weight(1f))
            }
        }
        return
    }

    val rows = categories.chunked(2)
    items(rows) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp, top = 6.dp),
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
                CircularProgressIndicator(color = VintageGray)
            }
        }
        is CategorySheetState.Error -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.explore_playlists_error), color = VintageGray, fontSize = 14.sp)
            }
        }
        is CategorySheetState.Success -> {
            val result = state.result
            LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
                item {
                    Text(
                        text = result.title,
                        color = VintageWhite,
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
                .size(44.dp)
                .clip(CircleShape)
                .background(VintageBgDark)
        )
        Spacer(Modifier.width(14.dp))
        TrackInfo(
            title = artist.name,
            artist = stringResource(R.string.label_artist),
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
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VintageBgDark)
        )
        Spacer(Modifier.width(14.dp))
        val albumLabel = stringResource(R.string.label_album)
        TrackInfo(
            title = album.title,
            artist = buildString {
                append(albumLabel)
                if (!album.year.isNullOrBlank()) append("  •  ${album.year}")
            },
            titleSize = 15.sp,
            titleWeight = FontWeight.SemiBold
        )
    }
}
