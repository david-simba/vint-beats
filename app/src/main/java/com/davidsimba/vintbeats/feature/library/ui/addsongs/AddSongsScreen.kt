package com.davidsimba.vintbeats.feature.library.ui.addsongs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.search.ui.components.SearchField
import com.davidsimba.vintbeats.shared.components.VintCheckbox
import com.davidsimba.vintbeats.shared.components.cards.TrackCard
import com.davidsimba.vintbeats.shared.theme.VintageGray

@Composable
fun AddSongsScreen(
    onBack: () -> Unit,
    viewModel: AddSongsViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)

    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val addedTrackIds by viewModel.addedTrackIds.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
            stickyHeader {
                SearchField(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    autoFocus = true,
                    leadingIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.action_back),
                                tint = com.davidsimba.vintbeats.shared.theme.VintageBgDark
                            )
                        }
                    }
                )
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VintageGray)
                    }
                }
            } else {
                items(results) { track ->
                    val isAdded = addedTrackIds.contains(track.id)
                    TrackCard(
                        title = track.title,
                        artist = track.artist,
                        thumbnailUrl = track.albumImageUrl,
                        onClick = null,
                        trailingContent = {
                            VintCheckbox(
                                checked = isAdded,
                                onCheckedChange = { if (!isAdded) viewModel.addTrack(track) }
                            )
                        }
                    )
                }
            }
        }
    }
}
