package com.davidsimba.vintbeats.shared.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageWhiteMid

@Composable
fun Card(
    modifier: Modifier = Modifier,
    backgroundColor: Color = VintageBgBase,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, VintageWhiteMid.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 32.dp),
        content = content
    )
}
