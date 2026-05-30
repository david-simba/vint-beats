package com.davidsimba.vintbeats.feature.playlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun PlaylistScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onPlayAll: (List<Track>) -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = VintageWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        when (val state = uiState) {
            is PlaylistUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VintageGray)
                }
            }
            is PlaylistUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = VintageGray, fontSize = 14.sp)
                }
            }
            is PlaylistUiState.Success -> {
                val detail = state.detail
                LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (detail.thumbnailUrl != null) {
                                AsyncImage(
                                    model = detail.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(VintageBgDark)
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                            Text(
                                text = detail.title,
                                color = VintageWhite,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${detail.tracks.size} songs",
                                color = VintageGray,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            if (detail.tracks.isNotEmpty()) {
                                Button(
                                    onClick = { onPlayAll(detail.tracks) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VintageRedLight),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text("Play All", fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                    items(detail.tracks) { track ->
                        TrackCard(
                            title = track.title,
                            artist = track.artist,
                            thumbnailUrl = track.albumImageUrl,
                            onClick = { onTrackSelected(track, detail.tracks) }
                        )
                    }
                }
            }
        }
    }
}
