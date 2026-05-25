package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun PlayerLyricsScreen(
    lines: List<LyricLine>,
    positionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    track: Track?,
    onClose: () -> Unit,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
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
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Close lyrics",
                    tint = VintageWhitePure.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "Lyrics",
                color = VintageWhiteWarm,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.size(48.dp))
        }

        if (lines.isEmpty()) {
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
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                itemsIndexed(lines) { index, line ->
                    val isCurrent = index == currentIndex
                    val color by animateColorAsState(
                        targetValue = if (isCurrent) VintageWhitePure else VintageWhitePure.copy(alpha = 0.35f),
                        animationSpec = tween(300),
                        label = "lyric_full_$index"
                    )
                    Text(
                        text = line.text,
                        color = color,
                        fontSize = if (isCurrent) 20.sp else 18.sp,
                        fontWeight = if (isCurrent) FontWeight.Black else FontWeight.SemiBold,
                        lineHeight = 36.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeek(line.timeMs) }
                            .padding(vertical = 6.dp)
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track?.title ?: "",
                    color = VintageWhitePure,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track?.artist ?: "",
                    color = VintageWhitePure.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
