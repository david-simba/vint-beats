package com.davidsimba.vintbeats.feature.home.ui.components

import com.davidsimba.vintbeats.core.designsystem.R as DsR

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.feature.home.domain.ArtistRadioItem
import com.davidsimba.vintbeats.core.designsystem.components.cards.RadioCard
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.feature.home.R

@Composable
fun ArtistRadioSection(
    radios: List<ArtistRadioItem>,
    onRadioSelected: (ArtistRadioItem) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            text = stringResource(DsR.string.home_radio),
            color = TextoPrincipal,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(radios, key = { it.artistId }) { radio ->
                val artistLine = radio.tracks
                    .map { it.artist }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .joinToString(" · ")
                Column {
                    RadioCard(
                        artistId = radio.artistId,
                        artistName = radio.artistName,
                        images = radio.artistImages,
                        onClick = { onRadioSelected(radio) },
                        modifier = Modifier.width(180.dp),
                    )
                    CardSubtitle(text = stringResource(R.string.home_playlist_de, artistLine))
                }
            }
        }
    }
}
