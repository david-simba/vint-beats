package com.davidsimba.vintbeats.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.home.R
import com.davidsimba.vintbeats.feature.home.ui.components.HomeTrackOptionsSheet
import com.davidsimba.vintbeats.feature.home.ui.components.ParaTiRow

/** Lista completa de "Para ti". Comparte el [HomeViewModel] del Home para no volver a pedir el feed. */
@Composable
fun QuickMixScreen(
    playingTrackId: String? = null,
    onBack: () -> Unit,
    onTrackSelected: (Track, List<Track>) -> Unit,
    onNavigateToAddToPlaylist: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tracks = (uiState as? HomeUiState.Success)?.quickMix.orEmpty()
    var selectedTrack by remember { mutableStateOf<Track?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding(),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 20.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.home_volver),
                        tint = TextoPrincipal,
                    )
                }
                Text(
                    text = stringResource(R.string.home_seleccion_rapida),
                    color = TextoPrincipal,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
        itemsIndexed(tracks, key = { _, track -> track.id }) { index, track ->
            ParaTiRow(
                track = track,
                onClick = { onTrackSelected(track, tracks.drop(index + 1)) },
                onMenuClick = { selectedTrack = track },
            )
        }
    }

    selectedTrack?.let { track ->
        HomeTrackOptionsSheet(
            track = track,
            playingTrackId = playingTrackId,
            onDismiss = { selectedTrack = null },
            onNavigateToAddToPlaylist = onNavigateToAddToPlaylist,
        )
    }
}
