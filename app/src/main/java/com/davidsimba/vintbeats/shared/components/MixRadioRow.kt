package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.shared.components.cards.PlaylistCard
import com.davidsimba.vintbeats.shared.components.cards.RadioCard

@Composable
fun MixRadioRow(
    artistId: String,
    artistName: String,
    mixThumbnailUrl: String?,
    radioImages: List<String>,
    onMixClick: () -> Unit,
    onRadioClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            PlaylistCard(
                id = artistId,
                name = artistName,
                thumbnailUrl = mixThumbnailUrl,
                onClick = onMixClick,
            )
        }
        item {
            RadioCard(
                artistId = artistId,
                artistName = artistName,
                images = radioImages,
                onClick = onRadioClick,
            )
        }
    }
}
