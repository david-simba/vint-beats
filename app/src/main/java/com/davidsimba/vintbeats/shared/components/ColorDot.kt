package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun ColorDot(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .then(
                if (selected) Modifier.border(2.dp, VintageWhite, RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable { onClick() }
    )
}