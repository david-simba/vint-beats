package com.davidsimba.vintbeats.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import com.davidsimba.vintbeats.feature.search.ui.components.SearchField

@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val canFinish = selected.size >= 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VintageBgDark)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 24.dp, bottom = 8.dp)) {
            Text(
                text = "Elige tus artistas",
                color = VintageWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Selecciona al menos 3 para personalizar tu home",
                color = VintageGrayMid,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (selected.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(selected) { artist ->
                    SelectedArtistChip(artist = artist, onRemove = { viewModel.toggleArtist(artist) })
                }
            }
        } else {
            Spacer(Modifier.height(12.dp))
        }

        SearchField(
            query = query,
            onQueryChange = viewModel::onQueryChange,
            autoFocus = false,
        )

        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = VintageWhite,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(results) { artist ->
                        ArtistRow(
                            artist = artist,
                            isSelected = viewModel.isSelected(artist.id),
                            onClick = { viewModel.toggleArtist(artist) }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.complete(onDone) },
            enabled = canFinish,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VintageWhite,
                contentColor = VintageBgDark,
                disabledContainerColor = VintageGrayDeep.copy(alpha = 0.3f),
                disabledContentColor = VintageGrayMid,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(52.dp)
        ) {
            Text(
                text = if (canFinish) "Listo (${selected.size})" else "Selecciona ${3 - selected.size} más",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun SelectedArtistChip(artist: Artist, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(VintageWhite.copy(alpha = 0.1f))
            .padding(start = 6.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AsyncImage(
            model = artist.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(24.dp).clip(CircleShape)
        )
        Text(text = artist.name, color = VintageWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = VintageGrayMid,
            modifier = Modifier.size(14.dp).clickable { onRemove() }
        )
    }
}

@Composable
private fun ArtistRow(artist: Artist, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(VintageGrayDeep)
        ) {
            AsyncImage(
                model = artist.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = artist.name,
            color = VintageWhiteWarm,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = VintageWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
