package com.davidsimba.vintbeats.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette
import com.davidsimba.vintbeats.shared.components.cassette.CassetteView
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun HomeScreen(
    onCreateCassette: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val cassettes by viewModel.cassettes.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Cassettes",
                color = VintageWhiteWarm,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onCreateCassette) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Create cassette",
                    tint = VintageGrayMid
                )
            }
        }

        if (cassettes.isEmpty()) {
            EmptyState(onCreateCassette = onCreateCassette)
        } else {
            CassetteCarousel(cassettes = cassettes)
        }
    }
}

@Composable
private fun CassetteCarousel(cassettes: List<SavedCassette>) {
    val pagerState = rememberPagerState(pageCount = { cassettes.size })

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            val cassette = cassettes[page]
            CassetteView(
                isPlaying = false,
                isFloating = true,
                cassetteColor = cassette.cassetteColor,
                lineColor = cassette.lineColor,
                drawRainbow = cassette.isRainbow,
                rotationDegrees = 270f,
            )
        }

        val current = cassettes.getOrNull(pagerState.currentPage)
        if (current != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = current.trackTitle,
                    color = VintageWhitePure,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                val subtitle = if (current.trackDurationText.isNotEmpty())
                    "${current.trackArtist} • ${current.trackDurationText}"
                else current.trackArtist
                Text(
                    text = subtitle,
                    color = VintageGrayMid,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (cassettes.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(cassettes.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (selected) 8.dp else 5.dp)
                            .clip(CircleShape)
                            .background(if (selected) VintageWhitePure else VintageGrayDeep)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun EmptyState(onCreateCassette: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            CassetteView(
                isPlaying = false,
                isFloating = true,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = "No cassettes yet",
                color = VintageWhiteWarm,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Search for a song and create\nyour first cassette",
                color = VintageGrayMid,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            androidx.compose.material3.Button(
                onClick = onCreateCassette,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = VintageWhiteWarm.copy(alpha = 0.15f),
                    contentColor = VintageWhiteWarm
                )
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(6.dp))
                Text("Create Cassette", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
