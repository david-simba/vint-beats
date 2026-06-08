package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.home.domain.ArtistRadioItem
import com.davidsimba.vintbeats.shared.components.cards.RadioCard
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun ArtistRadioSection(
    radios: List<ArtistRadioItem>,
    onRadioSelected: (ArtistRadioItem) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            text = stringResource(R.string.home_radio),
            color = VintageWhiteWarm,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
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
                    CardSubtitle(text = "Playlist · $artistLine")
                }
            }
        }
    }
}
