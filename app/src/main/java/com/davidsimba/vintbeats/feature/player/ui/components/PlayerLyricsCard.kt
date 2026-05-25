package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.shared.components.cards.Card
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun PlayerLyricsCard(
    lines: List<LyricLine>,
    positionMs: Long,
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

    Card(modifier = modifier) {
        Text(
            text = "Lyrics",
            color = VintageWhiteWarm,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        HorizontalDivider(
            color = VintageGrayDeep.copy(alpha = 1f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        if (lines.isEmpty()) {
            Text(
                text = "No lyrics available for this song.",
                color = VintageWhitePure.copy(alpha = 0.4f),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                itemsIndexed(lines) { index, line ->
                    val isCurrent = index == currentIndex
                    val color by animateColorAsState(
                        targetValue = if (isCurrent) VintageWhitePure
                                      else VintageWhitePure.copy(alpha = 0.32f),
                        animationSpec = tween(300),
                        label = "lyric_line_color"
                    )
                    Text(
                        text = line.text,
                        color = color,
                        fontSize = 14.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    )
                }
            }
        }
    }
}
