package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import kotlin.math.roundToInt

/**
 * Portada como cuadro. Mantiene el gesto de deslizar entre canciones: las portadas anterior y siguiente
 * entran por los lados, igual que en [PlayerBackground].
 */
@Composable
fun PlayerCover(
    currentImageUrl: String?,
    nextImageUrl: String?,
    previousImageUrl: String?,
    offsetX: Float,
    componentWidth: Float,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .shadow(16.dp, shape)
            .clip(shape)
            .background(Superficie)
    ) {
        AsyncImage(
            model = previousImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((-componentWidth + offsetX).roundToInt(), 0) }
        )
        AsyncImage(
            model = nextImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((componentWidth + offsetX).roundToInt(), 0) }
        )
        AsyncImage(
            model = currentImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
        )
    }
}
