package com.davidsimba.vintbeats.dj

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import com.davidsimba.vintbeats.core.designsystem.theme.colorAcento

private val OrbSize = 200.dp
private val HaloSize = 480.dp

/**
 * Colibrí central del DJ: en reposo ([live] = false) o al aire ([live] = true).
 * Con [busy] el anillo punteado gira más rápido y el círculo no acepta toques.
 */
@Composable
fun DjOrb(
    live: Boolean,
    busy: Boolean,
    /** Color del halo (en vivo toma el de la portada). El círculo y los anillos mantienen el color de marca. */
    haloTint: Color = colorAcento,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // "Reducir animaciones" del sistema deja la escala de animaciones en 0.
    val reduceMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    val transition = rememberInfiniteTransition(label = "dj_orb")
    val breathe by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (live) 2000 else 3000), RepeatMode.Reverse),
        label = "breathe",
    )
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(if (busy) 3000 else 14000, easing = LinearEasing)),
        label = "spin",
    )
    val t = if (reduceMotion) 0.5f else breathe
    val haloScale = if (live) 1f + 0.08f * t else 1f + 0.04f * t
    val haloAlpha = if (live) 1f else 0.5f + 0.25f * t
    val haloColor = haloTint.copy(alpha = if (live) 0.42f else 0.19f)

    Box(modifier = modifier.size(OrbSize), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .requiredSize(HaloSize)
                .graphicsLayer {
                    scaleX = haloScale
                    scaleY = haloScale
                    alpha = haloAlpha
                }
                .background(Brush.radialGradient(listOf(haloColor, Color.Transparent)), CircleShape),
        )
        Box(
            modifier = Modifier
                .size(OrbSize)
                .then(
                    if (live) {
                        Modifier.drawBehind {
                            val r = size.minDimension / 2
                            drawCircle(colorAcento.copy(alpha = 0.08f), radius = r + 22.dp.toPx())
                            drawCircle(colorAcento.copy(alpha = 0.2f), radius = r + 10.dp.toPx())
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(OrbSize)
                    .clip(CircleShape)
                    .background(if (live) colorAcento else Superficie)
                    .then(if (live) Modifier else Modifier.border(2.dp, colorAcento.copy(alpha = 0.4f), CircleShape))
                    .clickable(enabled = !busy, role = Role.Button, onClick = onClick)
                    .semantics { this.contentDescription = contentDescription },
                contentAlignment = Alignment.Center,
            ) {
                if (live) {
                    Canvas(modifier = Modifier.size(OrbSize)) {
                        val inset = 14.dp.toPx()
                        rotate(if (reduceMotion) 0f else spin) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.35f),
                                radius = size.minDimension / 2 - inset,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 8.dp.toPx())),
                                ),
                            )
                        }
                    }
                }
                Icon(
                    painter = painterResource(R.drawable.ic_dj),
                    contentDescription = null,
                    tint = if (live) Color.White else colorAcento,
                    modifier = Modifier.size(84.dp).graphicsLayer { alpha = if (busy) 0.55f + 0.45f * t else 1f },
                )
            }
        }
    }
}
