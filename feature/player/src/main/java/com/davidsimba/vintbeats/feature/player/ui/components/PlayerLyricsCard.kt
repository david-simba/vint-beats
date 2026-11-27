package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.model.LyricLine
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.feature.player.R
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBgBase
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhite

@Composable
fun PlayerLyricsCard(
    modifier: Modifier = Modifier,
    lines: List<LyricLine>,
    isLoading: Boolean = false,
    currentIndex: Int,
    cardBgColor: Color = VintageBgBase,
    onExpand: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    var initialScrollDone by remember(lines) { mutableStateOf(false) }

    LaunchedEffect(lines) {
        listState.scrollToItem(0)
    }

    LaunchedEffect(currentIndex) {
        if (currentIndex < 0) return@LaunchedEffect
        if (!initialScrollDone) {
            listState.scrollToItem(maxOf(0, currentIndex - 2))
            initialScrollDone = true
        } else if (!listState.isScrollInProgress) {
            listState.animateScrollToItem(maxOf(0, currentIndex - 2))
        }
    }

    PlayerSectionCard(backgroundColor = cardBgColor, modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.player_lyrics_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            IconButton(
                onClick = onExpand,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fullscreen,
                    contentDescription = stringResource(R.string.player_expand_lyrics),
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        if (isLoading) {
            LyricsSkeleton()
        } else if (lines.isEmpty()) {
            Text(
                text = stringResource(R.string.lyrics_none),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    userScrollEnabled = false,
                ) {
                    itemsIndexed(lines, key = { index, _ -> index }) { index, line ->
                        val alpha by animateFloatAsState(
                            targetValue = when {
                                index == currentIndex -> 1f
                                index > currentIndex -> 0.55f
                                else -> 0.3f
                            },
                            animationSpec = tween(400),
                            label = "lyric_alpha"
                        )
                        Text(
                            text = line.text,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 26.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .padding(end = 8.dp)
                                .graphicsLayer { this.alpha = alpha }
                        )
                    }
                    item { Spacer(Modifier.height(60.dp)) }
                }

                val topAlpha by animateFloatAsState(
                    targetValue = if (listState.firstVisibleItemIndex > 0 ||
                        listState.firstVisibleItemScrollOffset > 0) 1f else 0f,
                    animationSpec = tween(200),
                    label = "top_fade"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(cardBgColor.copy(alpha = topAlpha), Color.Transparent)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, cardBgColor)))
                )
            }
        }
    }
}

@Composable
private fun LyricsSkeleton() {
    val alpha by rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "skeleton_alpha"
    )
    val widths = remember { listOf(0.75f, 0.55f, 0.85f, 0.45f, 0.65f) }
    Column(modifier = Modifier.fillMaxWidth().height(120.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        widths.forEach { fraction ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(12.dp)
                    .background(
                        color = VintageWhite.copy(alpha = alpha),
                        shape = RoundedCornerShape(6.dp)
                    )
            )
        }
    }
}
