package com.davidsimba.vintbeats.feature.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun LibraryScreen(
    onCassetteClick: (Int) -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val cassettes by viewModel.cassettes.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = "Library",
            color = VintageWhitePure,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)
        )

        if (cassettes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No songs saved yet",
                    color = VintageGrayMid,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(cassettes, key = { it.id }) { cassette ->
                    CassetteListItem(
                        cassette = cassette,
                        onClick = { onCassetteClick(cassette.id) }
                    )
                    HorizontalDivider(
                        color = VintageGrayDeep.copy(alpha = 0.4f),
                        thickness = 0.5.dp
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun CassetteListItem(cassette: SavedCassette, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(VintageGrayDeep)
        ) {
            AsyncImage(
                model = cassette.trackThumbnailUrl,
                contentDescription = cassette.trackTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }

        val subtitle = if (cassette.trackDurationText.isNotEmpty())
            "${cassette.trackArtist} • ${cassette.trackDurationText}"
        else cassette.trackArtist
        TrackInfo(
            title = cassette.trackTitle,
            artist = subtitle,
            modifier = Modifier.weight(1f),
            titleSize = 14.sp,
            artistSize = 12.sp
        )
    }
}
