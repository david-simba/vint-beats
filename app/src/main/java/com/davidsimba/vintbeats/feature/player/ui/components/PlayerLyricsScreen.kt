package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.components.TrackInfo
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun PlayerLyricsScreen(
    modifier: Modifier = Modifier,
    lines: List<LyricLine>,
    isLoading: Boolean = false,
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    track: Track?,
    onClose: () -> Unit,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
) {
    val currentIndex = remember(lines, positionMs) {
        if (lines.isEmpty()) -1
        else lines.indexOfLast { it.timeMs <= positionMs }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0 && !listState.isScrollInProgress) {
            listState.animateScrollToItem(
                index = (currentIndex - 2).coerceAtLeast(0)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = VintageWhitePure,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Lyrics",
                color = VintageWhiteWarm,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (isLoading) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val alpha by rememberInfiniteTransition(label = "skeleton").animateFloat(
                    initialValue = 0.12f,
                    targetValue = 0.3f,
                    animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
                    label = "skeleton_alpha_screen"
                )
                listOf(0.8f, 0.6f, 0.9f, 0.5f, 0.75f, 0.55f, 0.85f).forEach { fraction ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(14.dp)
                            .background(
                                color = VintageWhitePure.copy(alpha = alpha),
                                shape = RoundedCornerShape(7.dp)
                            )
                    )
                }
            }
        } else if (lines.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No lyrics available for this song.",
                    color = VintageWhitePure.copy(alpha = 0.4f),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 16.dp)
            ) {
                itemsIndexed(lines) { index, line ->
                    val isCurrent = index == currentIndex
                    val color by animateColorAsState(
                        targetValue = if (isCurrent) VintageWhitePure else VintageWhitePure.copy(alpha = 0.35f),
                        animationSpec = tween(250),
                        label = "lyric_full_color_$index"
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (isCurrent) 1.08f else 1f,
                        animationSpec = tween(250),
                        label = "lyric_full_scale_$index"
                    )
                    val linePad by animateFloatAsState(
                        targetValue = if (isCurrent) 12f else 8f,
                        animationSpec = tween(250),
                        label = "lyric_full_pad_$index"
                    )
                    Text(
                        text = line.text,
                        color = color,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeek(line.timeMs) }
                            .padding(top = linePad.dp, bottom = linePad.dp, end = 12.dp)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin(0f, 0.5f)
                            }
                    )
                }
            }
        }

        HorizontalDivider(color = VintageWhitePure.copy(alpha = 0.08f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = track?.albumImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            TrackInfo(
                title = track?.title ?: "",
                artist = track?.artist ?: "",
                modifier = Modifier.weight(1f),
                titleSize = 14.sp,
                artistSize = 12.sp,
                artistColor = VintageWhitePure.copy(alpha = 0.55f)
            )
            IconButton(onClick = onTogglePlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = VintageWhitePure,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        SeekBar(
            positionMs = positionMs,
            durationMs = durationMs,
            isLoading = false,
            accentColor = VintageRedLight,
            onSeek = onSeek,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        )
    }
}
