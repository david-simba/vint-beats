package com.davidsimba.vintbeats.feature.search.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.feature.search.domain.Track
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageGrayCool
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            TextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = {
                    Text("Search songs, artists...", color = VintageGrayCool)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = VintageGrayCool
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = VintageBlackMid,
                    unfocusedContainerColor = VintageBlackMid,
                    focusedTextColor = VintageWhitePure,
                    unfocusedTextColor = VintageGrayCool,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = VintageWhite,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when (val state = uiState) {
                is SearchUiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Find your next cassette", color = VintageWhitePure, fontSize = 14.sp)
                    }
                }
                is SearchUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VintageGrayCool)
                    }
                }
                is SearchUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = VintageWhitePure, fontSize = 14.sp)
                    }
                }
                is SearchUiState.Success -> {
                    LazyColumn {
                        items(state.tracks) { track ->
                            TrackItem(track = track)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.albumImageUrl,
            contentDescription = track.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                color = VintageWhitePure,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = track.artist,
                color = VintageGrayCool,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}