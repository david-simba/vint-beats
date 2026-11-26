package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.core.model.PlaylistItem
import com.davidsimba.vintbeats.feature.home.R

private val AVATAR_SIZE = 120.dp

@Composable
fun ArtistsSection(
    artists: List<PlaylistItem>,
    onArtistClick: (artistId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Text(
            text = stringResource(R.string.home_tus_artistas),
            color = TextoPrincipal,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
        )
        // Sin padding a la derecha: el carrusel llega hasta el borde de la pantalla.
        LazyRow(
            contentPadding = PaddingValues(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(artists, key = { it.id }) { artist ->
                val name = artist.artistName ?: artist.title
                val artistId = artist.artistId ?: artist.id
                val description = stringResource(R.string.home_ver_perfil, name)
                Column(
                    modifier = Modifier
                        .width(AVATAR_SIZE)
                        .clip(RoundedCornerShape(8.dp))
                        .semantics(mergeDescendants = true) { contentDescription = description }
                        .clickable(role = Role.Button) { onArtistClick(artistId) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AsyncImage(
                        model = artist.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(AVATAR_SIZE)
                            .clip(CircleShape)
                            .background(Superficie),
                    )
                    Text(
                        text = name,
                        color = TextoPrincipal,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
