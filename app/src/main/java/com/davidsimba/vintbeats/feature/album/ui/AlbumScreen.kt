package com.davidsimba.vintbeats.feature.album.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.album.data.AlbumDetail
import com.davidsimba.vintbeats.feature.album.ui.components.AlbumTrackItem
import com.davidsimba.vintbeats.shared.components.SectionLabel
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun AlbumScreen(
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onPlayAlbum: (List<Track>) -> Unit,
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val parallaxOffset by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == 0 }
                ?.offset?.toFloat() ?: 0f
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is AlbumUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = VintageGray
                )
            }

            is AlbumUiState.Error -> {
                Text(
                    text = state.message,
                    color = VintageWhite,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                )
            }

            is AlbumUiState.Success -> {
                LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
                    item {
                        AlbumHeader(
                            album = state.album,
                            parallaxOffset = parallaxOffset,
                            onPlay = {
                                val tracks = state.album.tracks
                                if (tracks.isNotEmpty()) onPlayAlbum(tracks)
                            }
                        )
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(VintageBgDark)
                        ) {
                            if (state.album.tracks.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                SectionLabel(stringResource(R.string.album_tracks))
                                state.album.tracks.forEachIndexed { index, track ->
                                    AlbumTrackItem(
                                        index = index + 1,
                                        track = track,
                                        onClick = { onTrackSelected(track, state.album.tracks.drop(index + 1)) }
                                    )
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.album_no_tracks),
                                    color = VintageGrayMid,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .zIndex(1f)
                .statusBarsPadding()
                .padding(8.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = stringResource(R.string.action_back),
                tint = VintageWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AlbumHeader(album: AlbumDetail, parallaxOffset: Float = 0f, onPlay: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clipToBounds()
    ) {
        AsyncImage(
            model = album.thumbnailUrl,
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
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = album.title,
                    color = VintageWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (album.artist.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = buildString {
                            append(album.artist)
                            if (!album.year.isNullOrBlank()) append("  •  ${album.year}")
                        },
                        color = VintageWhite.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (album.tracks.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .border(1.5.dp, VintageWhite.copy(alpha = 0.4f), CircleShape)
                ) {
                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.album_play),
                            tint = VintageWhite,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}
