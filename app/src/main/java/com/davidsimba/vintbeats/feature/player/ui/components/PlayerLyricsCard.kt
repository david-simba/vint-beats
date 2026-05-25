package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.shared.components.cards.Card
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun PlayerLyricsCard(
    lines: List<LyricLine>,
    positionMs: Long,
    modifier: Modifier = Modifier,
    cardBgColor: Color = VintageBgBase
) {
    val currentIndex = remember(lines, positionMs) {
        if (lines.isEmpty()) -1
        else lines.indexOfLast { it.timeMs <= positionMs }
    }

    val scrollState = rememberScrollState()
    val itemYPositions = remember { HashMap<Int, Int>() }
    var boxHeightPx by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0 && !scrollState.isScrollInProgress) {
            val targetY = itemYPositions[currentIndex] ?: return@LaunchedEffect
            scrollState.animateScrollTo((targetY - boxHeightPx / 3).coerceAtLeast(0))
        }
    }

    Card(modifier = modifier, backgroundColor = cardBgColor) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lyrics",
                color = VintageWhiteWarm,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Rounded.Fullscreen,
                contentDescription = "Expand lyrics",
                tint = VintageWhitePure.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp)
            )
        }

        if (lines.isEmpty()) {
            Text(
                text = "No lyrics available for this song.",
                color = VintageWhitePure.copy(alpha = 0.4f),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(250.dp).onSizeChanged { boxHeightPx = it.height }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState, enabled = false)
                ) {
                    lines.forEachIndexed { index, line ->
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
                            fontSize = if (isCurrent) 20.sp else 18.sp,
                            fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Light,
                            lineHeight = 22.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp, end = 12.dp)
                                .onGloballyPositioned { coords ->
                                    itemYPositions[index] = coords.positionInParent().y.toInt()
                                }
                        )
                    }
                    Spacer(Modifier.height(60.dp))
                }

                val topFadeAlpha by animateFloatAsState(
                    targetValue = if (scrollState.value > 0) 1f else 0f,
                    animationSpec = tween(200),
                    label = "top_fade"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    cardBgColor.copy(alpha = topFadeAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, cardBgColor))
                        )
                )
            }
        }
    }
}
