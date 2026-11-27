package com.davidsimba.vintbeats.feature.artist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import com.davidsimba.vintbeats.core.designsystem.theme.TextoSecundario
import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.feature.artist.R

private enum class DiscographyFilter { Albums, Singles, Collaborations }

private fun Album.filter(): DiscographyFilter = when (type?.lowercase()) {
    "single", "ep" -> DiscographyFilter.Singles
    "collaboration", "appears_on" -> DiscographyFilter.Collaborations
    else -> DiscographyFilter.Albums
}

@Composable
fun ArtistDiscographySection(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Álbumes y "Sencillos y EP" se muestran siempre (maqueta hasta que el backend envíe el tipo);
    // Colaboraciones solo si hay datos.
    val available = DiscographyFilter.entries.filter { f ->
        f != DiscographyFilter.Collaborations || albums.any { it.filter() == f }
    }
    var selected by remember(available) { mutableStateOf(available.first()) }
    val visible = albums.filter { it.filter() == selected }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.artist_discography),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
            // Maqueta: aún no hay endpoint ni pantalla de discografía completa.
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clickable(role = Role.Button) { }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.artist_see_all),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(available) { filter ->
                FilterPill(
                    text = stringResource(
                        when (filter) {
                            DiscographyFilter.Albums -> R.string.artist_albums
                            DiscographyFilter.Singles -> R.string.artist_singles
                            DiscographyFilter.Collaborations -> R.string.artist_collaborations
                        }
                    ),
                    selected = filter == selected,
                    onClick = { selected = filter },
                )
            }
        }
        if (visible.isEmpty()) {
            Text(
                text = stringResource(R.string.artist_no_singles),
                color = TextoSecundario,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
            )
        }
        LazyRow(
            modifier = Modifier.padding(top = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(visible, key = { it.id }) { album ->
                DiscographyCard(album = album, onClick = { onAlbumClick(album) })
            }
        }
    }
}

@Composable
private fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .clickable(role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val shape = CircleShape
        Box(
            modifier = Modifier
                .height(36.dp)
                .clip(shape)
                .then(
                    if (selected) Modifier.background(Color(0xFFF4F4F5))
                    else Modifier.border(1.dp, Color(0xFF3F3F46), shape)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = if (selected) Fondo else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DiscographyCard(album: Album, onClick: () -> Unit) {
    val typeRes = when (album.type?.lowercase()) {
        "single" -> R.string.artist_type_single
        "ep" -> R.string.artist_type_ep
        else -> R.string.artist_type_album
    }
    val typeLabel = stringResource(typeRes)
    val subtitle = album.year?.takeIf { it.isNotBlank() }
        ?.let { stringResource(R.string.artist_type_year, typeLabel, it) } ?: typeLabel

    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Superficie)
        ) {
            AsyncImage(
                model = album.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
        Text(
            text = album.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = subtitle,
            color = TextoSecundario,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
