package com.davidsimba.vintbeats.shared.components.cassette

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.shared.theme.VintageBlack
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageBrownLight
import com.davidsimba.vintbeats.shared.theme.VintageBrownMid
import com.davidsimba.vintbeats.shared.theme.VintageGray
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageBlueLight
import com.davidsimba.vintbeats.shared.theme.VintageGreenLight
import com.davidsimba.vintbeats.shared.theme.VintageOrangeLight
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import com.davidsimba.vintbeats.shared.theme.VintageYellowLight

internal fun DrawScope.drawCassetteBody(w: Float, h: Float) {
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(0f, 0f, w, h),
                cornerRadius = CornerRadius(h * 0.05f)
            )
        )
    }
    drawPath(path, VintageBlackMid)
}

internal fun DrawScope.drawLabel(w: Float, h: Float) {
    drawRoundRect(
        color = VintageWhite,
        topLeft = Offset(w * 0.06f, h * 0.06f),
        size = Size(w * 0.88f, h * 0.7f),
        cornerRadius = CornerRadius(h * 0.03f)
    )
}

internal fun DrawScope.drawStripes(w: Float, h: Float) {
    val stripeColors = listOf(
        VintageRedLight, VintageOrangeLight, VintageYellowLight,
        VintageGreenLight, VintageBlueLight
    )
    val top = h * 0.32f
    val stripeHeight = h * 0.3f / stripeColors.size
    val startX = w * 0.06f
    val stripeWidth = w * 0.88f

    stripeColors.forEachIndexed { index, color ->
        drawRect(
            color = color,
            topLeft = Offset(startX, top + index * stripeHeight),
            size = Size(stripeWidth, stripeHeight)
        )
    }
}

internal fun DrawScope.drawTapeWindow(w: Float, h: Float) {
    drawRoundRect(
        color = VintageBlack,
        topLeft = Offset(w * 0.2f, h * 0.32f),
        size = Size(w * 0.6f, h * 0.3f),
        cornerRadius = CornerRadius(h * 0.06f)
    )
    drawRect(
        color = VintageBrownMid,
        topLeft = Offset(w * 0.42f, h * 0.38f),
        size = Size(w * 0.16f, h * 0.12f)
    )
    drawLine(
        color = VintageBrownLight,
        start = Offset(w * 0.43f, h * 0.42f),
        end = Offset(w * 0.57f, h * 0.42f),
        strokeWidth = h * 0.015f
    )
}

internal fun DrawScope.drawReel(
    centerX: Float,
    centerY: Float,
    radius: Float,
    angle: Float
) {
    drawCircle(
        color = VintageWhite,
        radius = radius,
        center = Offset(centerX, centerY),
    )
    drawCircle(color = VintageWhite, radius = radius * 0.72f, center = Offset(centerX, centerY))
    rotate(degrees = angle, pivot = Offset(centerX, centerY)) {
        repeat(6) { i ->
            rotate(degrees = i * 60f, pivot = Offset(centerX, centerY)) {
                val tabW = radius * 0.22f
                val tabH = radius * 0.28f
                drawRect(
                    color = VintageGrayMid,
                    topLeft = Offset(centerX - tabW / 2f, centerY - radius * 0.68f),
                    size = Size(tabW, tabH)
                )
            }
        }
    }
}

internal fun DrawScope.drawBottomDetail(w: Float, h: Float) {
    drawRoundRect(
        color = VintageBlackMid,
        topLeft = Offset(w * 0.25f, h * 0.75f),
        size = Size(w * 0.50f, h * 0.20f),
        cornerRadius = CornerRadius(h * 0.02f)
    )
    listOf(0.35f, 0.47f, 0.59f).forEach { x ->
        drawRoundRect(
            color = VintageWhiteWarm,
            topLeft = Offset(w * x, h * 0.80f),
            size = Size(w * 0.08f, h * 0.10f),
            cornerRadius = CornerRadius(h * 0.01f)
        )
    }
}

internal fun DrawScope.drawScrews(w: Float, h: Float) {
    listOf(
        Offset(w * 0.03f, h * 0.06f),
        Offset(w * 0.97f, h * 0.06f),
        Offset(w * 0.08f, h * 0.90f),
        Offset(w * 0.92f, h * 0.90f),
    ).forEach { pos ->
        drawCircle(color = VintageGray, radius = h * 0.03f, center = pos)
        drawCircle(color = VintageGrayDeep, radius = h * 0.015f, center = pos)
    }
}
