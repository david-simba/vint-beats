package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.shared.components.shimmerBrush

@Composable
fun QuickMixSkeleton() {
    val brush = shimmerBrush()

    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .width(110.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
        Spacer(Modifier.height(14.dp))
        repeat(4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(brush)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(13.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(11.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
fun HomeSectionSkeleton() {
    val brush = shimmerBrush()

    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .width(130.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(3) {
                Column {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.matchParentSize()) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(brush))
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(brush))
                        }
                        // App icon placeholder
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 6.dp, start = 6.dp)
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                        // "This is" label
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 14.dp)
                                .width(36.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                        // Artist image block
                        Box(
                            modifier = Modifier
                                .width(108.dp)
                                .height(100.dp)
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(2.dp))
                                .background(brush)
                        )
                        // Artist name
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .width(90.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                    }
                    // CardSubtitle placeholder
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistRadioSkeleton() {
    val brush = shimmerBrush()

    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .width(60.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(brush)
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(3) {
                Column {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.matchParentSize()) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(brush))
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(brush))
                        }
                        // App icon placeholder
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 12.dp, start = 6.dp)
                                .size(24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                        // "Radio" label
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 12.dp)
                                .width(36.dp)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                        // 3 circles
                        Box(
                            modifier = Modifier
                                .width(175.dp)
                                .height(90.dp)
                                .align(Alignment.Center)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                                    .align(Alignment.CenterStart)
                            )
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                                    .align(Alignment.Center)
                            )
                            Box(
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(CircleShape)
                                    .background(brush)
                                    .align(Alignment.CenterEnd)
                            )
                        }
                        // Artist name
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(bottom = 12.dp, start = 12.dp)
                                .width(80.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                    }
                    // CardSubtitle placeholder
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}
