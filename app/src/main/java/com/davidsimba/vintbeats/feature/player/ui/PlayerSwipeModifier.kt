package com.davidsimba.vintbeats.feature.player.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.playerSwipeGesture(
    offsetX: Animatable<Float, AnimationVector1D>,
    componentWidth: Float,
    enabled: Boolean,
    scope: CoroutineScope,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
): Modifier = this.pointerInput(enabled) {
    if (!enabled) return@pointerInput
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var cumulX = 0f
        var cumulY = 0f
        var isHorizontal: Boolean? = null

        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            val dx = change.position.x - change.previousPosition.x
            val dy = change.position.y - change.previousPosition.y
            cumulX += dx
            cumulY += dy

            if (!change.pressed) {
                if (isHorizontal == true) {
                    when {
                        offsetX.value < -(componentWidth * 0.4f) -> {
                            change.consume()
                            onSkipNext()
                            scope.launch {
                                offsetX.animateTo(-componentWidth, tween(150))
                                offsetX.snapTo(0f)
                            }
                        }
                        offsetX.value > (componentWidth * 0.4f) -> {
                            change.consume()
                            onSkipPrevious()
                            scope.launch {
                                offsetX.animateTo(componentWidth, tween(150))
                                offsetX.snapTo(0f)
                            }
                        }
                        else -> scope.launch {
                            offsetX.animateTo(
                                0f,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        }
                    }
                }
                break
            }

            if (isHorizontal == null) {
                val absX = kotlin.math.abs(cumulX)
                val absY = kotlin.math.abs(cumulY)
                if (absX > viewConfiguration.touchSlop || absY > viewConfiguration.touchSlop) {
                    isHorizontal = absX >= absY
                    if (isHorizontal == false) scope.launch { offsetX.snapTo(0f) }
                }
            }

            if (isHorizontal == true) {
                val newOffset = (offsetX.value + dx).coerceIn(
                    -componentWidth,
                    componentWidth
                )
                scope.launch { offsetX.snapTo(newOffset) }
                change.consume()
            }
        }
    }
}
