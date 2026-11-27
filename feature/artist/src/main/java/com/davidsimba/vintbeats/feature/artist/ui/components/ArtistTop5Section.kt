package com.davidsimba.vintbeats.feature.artist.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import com.davidsimba.vintbeats.core.designsystem.theme.TextoSecundario
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.R

private val CoverSize = 150.dp

/** Otros artistas de la canción (sin el artista de la página); vacío si es solo de él. */
internal fun otherArtists(track: Track, pageArtist: String): String =
    track.artist.split(",", "&", " x ", " feat. ")
        .map { it.trim() }
        .filter { it.isNotBlank() && !it.equals(pageArtist.trim(), ignoreCase = true) }
        .joinToString(", ")

/** "Top 5 de <artista>": carrusel con las 5 canciones más populares. Se oculta si no hay canciones. */
@Composable
fun ArtistTop5Section(
    artistName: String,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onTrackLongClick: (Track) -> Unit,
    modifier: Modifier = Modifier,
) {
    val top = tracks.take(5)
    if (top.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.artist_top5_title, artistName),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            // Maqueta: aún no existe la pantalla con la lista completa de canciones populares.
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clickable(role = Role.Button) { }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.artist_see_all),
                    color = TextoSecundario,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        // Sin padding a la derecha: el carrusel llega al borde para que se note que hay más.
        LazyRow(
            contentPadding = PaddingValues(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(top, key = { _, t -> t.id }) { index, track ->
                Top5Item(
                    number = index + 1,
                    track = track,
                    subtitle = otherArtists(track, artistName),
                    pageArtist = artistName,
                    onClick = { onTrackClick(track) },
                    onLongClick = { onTrackLongClick(track) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Top5Item(
    number: Int,
    track: Track,
    subtitle: String,
    pageArtist: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val strokePx = with(LocalDensity.current) { 2.dp.toPx() }
    val description = stringResource(
        R.string.artist_top5_item,
        number,
        track.title,
        subtitle.ifBlank { pageArtist },
    )
    val longClickLabel = stringResource(R.string.artist_track_options, track.title)

    Row {
        // Número decorativo: contorno, siempre detrás de la portada.
        Box(modifier = Modifier.height(CoverSize), contentAlignment = Alignment.BottomStart) {
            Text(
                text = number.toString(),
                style = TextStyle(
                    fontSize = 130.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-8).sp,
                    lineHeight = 104.sp,
                    color = Color(0xFF52525B),
                    drawStyle = Stroke(width = strokePx),
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both,
                    ),
                ),
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .wrapContentHeight(Alignment.Bottom, unbounded = true)
                    // Baja el número hasta que su base coincida con la de la portada.
                    .offset(y = 30.dp)
                    .clearAndSetSemantics { },
            )
        }
        Column(
            modifier = Modifier
                .offset(x = (-26).dp)
                .zIndex(1f)
                .width(CoverSize)
                .semantics(mergeDescendants = true) { contentDescription = description }
                .combinedClickable(
                    role = Role.Button,
                    onClickLabel = null,
                    onLongClickLabel = longClickLabel,
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
        ) {
            Box(
                modifier = Modifier
                    .size(CoverSize)
                    // Sombra suave hacia la izquierda para separar la portada del número.
                    .drawBehind {
                        val w = 28.dp.toPx()
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)),
                                startX = -w,
                                endX = 0f,
                            ),
                            topLeft = Offset(-w, 14.dp.toPx()),
                            size = Size(w + 16.dp.toPx(), size.height - 28.dp.toPx()),
                            cornerRadius = CornerRadius(8.dp.toPx()),
                        )
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(Superficie)
            ) {
                AsyncImage(
                    model = track.albumImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = track.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // Si no hay otros artistas se reserva la línea para que todas las tarjetas queden alineadas.
            Text(
                text = subtitle.ifBlank { " " },
                color = TextoSecundario,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
