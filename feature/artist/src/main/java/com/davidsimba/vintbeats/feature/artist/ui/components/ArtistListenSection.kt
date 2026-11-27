package com.davidsimba.vintbeats.feature.artist.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.components.cards.PlaylistCard
import com.davidsimba.vintbeats.core.designsystem.components.cards.RadioCard
import com.davidsimba.vintbeats.core.designsystem.theme.TextoSecundario
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.ArtistMix
import com.davidsimba.vintbeats.core.model.ArtistRadio
import com.davidsimba.vintbeats.feature.artist.R

/** "Para escuchar": las mismas tarjetas del Home (playlist del artista y radio). */
@Composable
fun ArtistListenSection(
    artist: Artist,
    mix: ArtistMix?,
    mixThumbnailUrl: String?,
    radio: ArtistRadio?,
    onMixClick: () -> Unit,
    onRadioClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.artist_listen),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (mix != null) {
                item {
                    PlaylistCard(
                        id = artist.id,
                        name = artist.name,
                        thumbnailUrl = mixThumbnailUrl,
                        onClick = onMixClick,
                    )
                }
            }
            if (radio != null) {
                item {
                    val names = radio.tracks.map { it.artist }
                        .flatMap { it.split(",", "&") }
                        .map { it.trim() }
                        .filter { it.isNotBlank() && !it.equals(artist.name, ignoreCase = true) }
                        .distinct()
                        .take(2)
                    Column(modifier = Modifier.width(180.dp)) {
                        RadioCard(
                            artistId = artist.id,
                            artistName = artist.name,
                            images = radio.artistImages,
                            onClick = onRadioClick,
                        )
                        val subtitle = when (names.size) {
                            0 -> null
                            1 -> stringResource(R.string.artist_radio_with_one, names[0])
                            else -> stringResource(R.string.artist_radio_with_two, names[0], names[1])
                        }
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                color = TextoSecundario,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
