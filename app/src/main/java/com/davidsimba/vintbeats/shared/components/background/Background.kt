package com.davidsimba.vintbeats.shared.components.background

import android.graphics.drawable.BitmapDrawable
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
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun rememberPaletteColor(thumbnailUrl: String?): Color {
    val context = LocalContext.current
    val raw by produceState(VintageBgDark, thumbnailUrl) {
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
        hsl[2] = 0.18f
        value = Color(ColorUtils.HSLToColor(hsl))
    }
    return animateColorAsState(raw, animationSpec = tween(600), label = "paletteColor").value
}

@Composable
fun Background(
    modifier: Modifier = Modifier,
    thumbnailUrl: String? = null,
    horizontal: Boolean = false,
    artColorsOnly: Boolean = false
) {
    val context = LocalContext.current

    val colors by produceState(listOf(VintageBgDark, VintageBgBase), thumbnailUrl, artColorsOnly) {
        val url = thumbnailUrl ?: return@produceState
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .size(100, 100)
            .build()
        val result = context.imageLoader.execute(request) as? SuccessResult ?: return@produceState
        val bitmap = (result.drawable as? BitmapDrawable)?.bitmap ?: return@produceState
        val palette = withContext(Dispatchers.Default) { Palette.from(bitmap).generate() }

        if (artColorsOnly) {
            // Two darkest swatches from the art
            val c1 = (palette.darkVibrantSwatch ?: palette.darkMutedSwatch
                ?: palette.dominantSwatch)?.rgb?.let { Color(it) } ?: VintageBgDark
            val c2 = (palette.darkMutedSwatch ?: palette.darkVibrantSwatch
                ?: palette.dominantSwatch)?.rgb?.let { Color(it) } ?: VintageBgBase
            value = listOf(c1, c2)
        } else {
            val swatch = palette.darkVibrantSwatch ?: palette.darkMutedSwatch
                ?: palette.vibrantSwatch ?: palette.mutedSwatch ?: palette.dominantSwatch
            val rgb = swatch?.rgb ?: return@produceState
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(rgb, hsl)
            hsl[1] = hsl[1].coerceAtMost(0.45f)
            hsl[2] = 0.25f
            value = listOf(Color(ColorUtils.HSLToColor(hsl)), VintageBgBase)
        }
    }

    val c1 by animateColorAsState(colors[0], animationSpec = tween(600), label = "bgColor1")
    val c2 by animateColorAsState(colors[1], animationSpec = tween(600), label = "bgColor2")

    val brush = if (horizontal)
        Brush.horizontalGradient(listOf(c1, c2))
    else
        Brush.verticalGradient(listOf(c1, c2))

    Box(modifier = modifier.background(brush))
}
