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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.album.data.AlbumDetail
import com.davidsimba.vintbeats.feature.album.ui.components.AlbumTrackItem
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
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

    val appBarAlpha by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) return@derivedStateOf 1f
            val item = lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == 0 } ?: return@derivedStateOf 0f
            val offset = lazyListState.firstVisibleItemScrollOffset.toFloat()
            val fadeStart = item.size * 0.55f
            val fadeEnd = item.size * 0.7f
            ((offset - fadeStart) / (fadeEnd - fadeStart)).coerceIn(0f, 1f)
        }
    }

    val albumTitle = (uiState as? AlbumUiState.Success)?.album?.title.orEmpty()

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
                LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize().background(VintageBgDark)) {
                    item {
                        AlbumHeader(
                            album = state.album,
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

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .zIndex(1f)
                .background(VintageBgDark.copy(alpha = appBarAlpha))
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.action_back),
                        tint = VintageWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = albumTitle,
                    color = VintageWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(appBarAlpha)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .alpha(appBarAlpha),
                color = VintageWhite.copy(alpha = 0.12f)
            )
        }
    }
}

@Composable
private fun AlbumHeader(album: AlbumDetail, onPlay: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .clipToBounds()
    ) {
        AsyncImage(
            model = album.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.0f),
                            0.45f to Color.Black.copy(alpha = 0.1f),
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
