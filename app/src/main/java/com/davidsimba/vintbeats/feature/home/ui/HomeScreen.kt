package com.davidsimba.vintbeats.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.home.domain.HomeSectionPlaylists
import com.davidsimba.vintbeats.feature.home.domain.PlaylistItem
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen(
    onPlaylistSelected: (id: String, thumbnailUrl: String?) -> Unit = { _, _ -> },
    onNavigateToOnboarding: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val needsOnboarding by viewModel.needsOnboarding.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        val needsIt = viewModel.needsOnboarding.filterNotNull().first()
        if (needsIt && viewModel.tryConsumeOnboardingNavigation()) {
            onNavigateToOnboarding()
        }
    }
    LaunchedEffect(needsOnboarding) {
        if (needsOnboarding == false) viewModel.loadFeed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VintageBgDark)
            .statusBarsPadding()
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    color = VintageGray,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is HomeUiState.Empty -> {
                Text(
                    text = stringResource(R.string.home_empty),
                    color = VintageGrayMid,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp)
                )
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = if (userName.isNotBlank()) {
                                stringResource(R.string.home_greeting, userName)
                            } else {
                                stringResource(R.string.nav_home)
                            },
                            color = VintageWhite,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }
                    items(state.sections) { section ->
                        HomeSection(
                            section = section,
                            onPlaylistSelected = onPlaylistSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSection(
    section: HomeSectionPlaylists,
    onPlaylistSelected: (id: String, thumbnailUrl: String?) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            text = section.title,
            color = VintageWhiteWarm,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
        if (section.isPrimary) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(section.playlists) { playlist ->
                    ParaTiPlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistSelected(playlist.id, playlist.thumbnailUrl) }
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(section.playlists) { playlist ->
                    HomePlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistSelected(playlist.id, playlist.thumbnailUrl) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ParaTiPlaylistCard(playlist: PlaylistItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(155.dp)
            .height(215.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(VintageGrayDeep)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = playlist.thumbnailUrl,
            contentDescription = playlist.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.42f to Color.Transparent,
                        1f to VintageBgDark
                    )
                )
        )
        Text(
            text = playlist.title,
            color = VintageWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 18.sp,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 14.dp)
        )
    }
}

@Composable
private fun HomePlaylistCard(playlist: PlaylistItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(VintageGrayDeep)
        ) {
            AsyncImage(
                model = playlist.thumbnailUrl,
                contentDescription = playlist.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = playlist.title,
            color = VintageWhiteWarm,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
        Text(
            text = playlist.subtitle,
            color = VintageGrayMid,
            fontSize = 11.sp,
            maxLines = 1,
        )
    }
}
