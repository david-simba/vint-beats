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
import com.davidsimba.vintbeats.core.model.HomeSectionPlaylists
import com.davidsimba.vintbeats.core.designsystem.components.cards.PlaylistCard
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.feature.home.R

/** Las secciones del feed llegan del backend con su título en español; aquí se localizan las conocidas. */
internal fun isFansSection(title: String) = title.contains("fans", ignoreCase = true)

@Composable
private fun sectionTitle(title: String): String = when {
    isFansSection(title) -> stringResource(R.string.home_fans_tambien_escuchan)
    title.contains("descubre", ignoreCase = true) -> stringResource(R.string.home_descubre)
    else -> title
}

@Composable
fun PlaylistSection(
    section: HomeSectionPlaylists,
    onPlaylistSelected: (id: String, thumbnailUrl: String?, artistId: String?, artistName: String?) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        Text(
            text = sectionTitle(section.title),
            color = TextoPrincipal,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
        )
        // Sin padding a la derecha: el carrusel llega hasta el borde de la pantalla.
        LazyRow(
            contentPadding = PaddingValues(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(section.playlists, key = { it.id }) { playlist ->
                val showStripe = !section.title.lowercase().contains("descubre")
                Column {
                    PlaylistCard(
                        id = playlist.id,
                        name = playlist.artistName ?: playlist.title,
                        thumbnailUrl = playlist.thumbnailUrl,
                        showStripe = showStripe,
                        onClick = {
                            onPlaylistSelected(
                                playlist.id,
                                playlist.thumbnailUrl,
                                playlist.artistId,
                                playlist.artistName
                            )
                        }
                    )
                    if (showStripe) {
                        CardSubtitle(text = stringResource(R.string.home_playlist_de, playlist.artistName ?: playlist.title))
                    }
                }
            }
        }
    }
}
