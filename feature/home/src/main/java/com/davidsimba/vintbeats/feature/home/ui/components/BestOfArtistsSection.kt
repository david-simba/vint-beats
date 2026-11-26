package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.components.cards.PlaylistCard
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.core.model.PlaylistItem
import com.davidsimba.vintbeats.feature.home.R

/** Las tarjetas de siempre de la playlist del artista; la etiqueta ("This is" / "Lo mejor de") depende del idioma. */
@Composable
fun BestOfArtistsSection(
    artists: List<PlaylistItem>,
    onPlaylistSelected: (id: String, thumbnailUrl: String?, artistId: String?, artistName: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Text(
            text = stringResource(R.string.home_lo_mejor_de_tus_artistas),
            color = TextoPrincipal,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
        )
        // Sin padding a la derecha: el carrusel llega hasta el borde de la pantalla.
        LazyRow(
            contentPadding = PaddingValues(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(artists, key = { it.id }) { artist ->
                val name = artist.artistName ?: artist.title
                Column {
                    PlaylistCard(
                        id = artist.id,
                        name = name,
                        thumbnailUrl = artist.thumbnailUrl,
                        cornerRadius = 6.dp,
                        onClick = {
                            onPlaylistSelected(artist.id, artist.thumbnailUrl, artist.artistId, artist.artistName)
                        },
                    )
                    CardSubtitle(text = stringResource(R.string.home_playlist_de, name))
                }
            }
        }
    }
}
