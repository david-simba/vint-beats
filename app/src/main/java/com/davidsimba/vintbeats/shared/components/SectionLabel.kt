package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = VintageWhitePure,
        fontSize = 14.sp,
        letterSpacing = 1.sp,
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}