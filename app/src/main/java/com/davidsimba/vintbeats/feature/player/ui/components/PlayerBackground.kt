package com.davidsimba.vintbeats.feature.player.ui.components

import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.ColorUtils
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PlayerBackground(thumbnailUrl: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val dominantColor by produceState<Color>(VintageBgDark, thumbnailUrl) {
        val url = thumbnailUrl ?: return@produceState
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .size(100, 100)
            .build()
        val result = context.imageLoader.execute(request) as? SuccessResult ?: return@produceState
        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap ?: return@produceState
        val palette = withContext(Dispatchers.Default) { Palette.from(bitmap).generate() }
        val swatch = palette.darkVibrantSwatch ?: palette.darkMutedSwatch
            ?: palette.vibrantSwatch ?: palette.mutedSwatch ?: palette.dominantSwatch
        val rgb = swatch?.rgb ?: return@produceState
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(rgb, hsl)
        hsl[1] = hsl[1].coerceAtMost(0.45f)
        hsl[2] = 0.14f
        value = Color(ColorUtils.HSLToColor(hsl))
    }

    val animatedBgColor by animateColorAsState(
        targetValue = dominantColor,
        animationSpec = tween(600),
        label = "bgColor"
    )

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(colors = listOf(animatedBgColor, VintageBgBase))
        )
    )
}
