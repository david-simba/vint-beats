package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.shared.components.cards.Card
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun PlayerLyricsCard(
    modifier: Modifier = Modifier,
    lines: List<LyricLine>,
    isLoading: Boolean = false,
    positionMs: Long,
    cardBgColor: Color = VintageBgBase,
    onExpand: () -> Unit = {}
) {
    val currentIndex = remember(lines, positionMs) {
        if (lines.isEmpty()) -1
        else lines.indexOfLast { it.timeMs <= positionMs }
    }

    val scrollState = rememberScrollState()
    val itemYPositions = remember { mutableStateMapOf<Int, Int>() }
    var boxHeightPx by remember { mutableIntStateOf(0) }
    var initialScrollDone by remember(lines) { mutableStateOf(false) }

    LaunchedEffect(lines) {
        scrollState.scrollTo(0)
        itemYPositions.clear()
    }

    LaunchedEffect(currentIndex, itemYPositions.size) {
        if (currentIndex < 0) return@LaunchedEffect
        val targetY = itemYPositions[currentIndex] ?: return@LaunchedEffect
        val offset = (targetY - boxHeightPx / 3).coerceAtLeast(0)
        if (!initialScrollDone) {
            scrollState.scrollTo(offset)
            initialScrollDone = true
        } else if (!scrollState.isScrollInProgress) {
            scrollState.animateScrollTo(offset)
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
            IconButton(
                onClick = onExpand,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fullscreen,
                    contentDescription = "Expand lyrics",
                    tint = VintageWhite.copy(alpha = 0.45f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (isLoading) {
            LyricsSkeleton()
        } else if (lines.isEmpty()) {
            Text(
                text = "No lyrics available for this song.",
                color = VintageWhite.copy(alpha = 0.4f),
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
                            targetValue = if (isCurrent) VintageWhite
                                          else VintageWhite.copy(alpha = 0.32f),
                            animationSpec = tween(250),
                            label = "lyric_card_color_$index"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isCurrent) 1.06f else 1f,
                            animationSpec = tween(250),
                            label = "lyric_card_scale_$index"
                        )
                        val linePad by animateDpAsState(
                            targetValue = if (isCurrent) 12.dp else 6.dp,
                            animationSpec = tween(250),
                            label = "lyric_card_pad_$index"
                        )
                        Text(
                            text = line.text,
                            color = color,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 22.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = linePad, bottom = linePad, end = 12.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    transformOrigin = TransformOrigin(0f, 0.5f)
                                }
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
