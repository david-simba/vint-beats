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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.ExploreCategory
import com.davidsimba.vintbeats.core.model.PlaylistSummary
import com.davidsimba.vintbeats.feature.search.ui.CategorySheetState
import androidx.compose.material3.CircularProgressIndicator
import com.davidsimba.vintbeats.core.designsystem.components.track.TrackInfo
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBgDark
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGray
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhite
import com.davidsimba.vintbeats.feature.search.R

fun LazyListScope.exploreGrid(
    categories: List<ExploreCategory>,
    onCategoryClick: (ExploreCategory) -> Unit
) {
    item(key = "explorar_titulo") {
        SearchSectionTitle(
            text = stringResource(R.string.search_explore_title),
            modifier = Modifier.padding(top = 12.dp),
        )
    }
    if (categories.isEmpty()) {
        items(3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExploreCardSkeleton(modifier = Modifier.weight(1f))
                ExploreCardSkeleton(modifier = Modifier.weight(1f))
            }
        }
        return
    }

    val ordered = categories.seasonalLast()
    items(ordered.chunked(2)) { row ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            row.forEach { category ->
                val index = ordered.indexOf(category)
                val title = category.nameRes()?.let { stringResource(it) } ?: category.title
                ExploreCard(
                    title = title,
                    color = exploreCardColors[index % exploreCardColors.size],
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
