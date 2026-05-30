package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun Header(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        color = VintageWhite,
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 14.dp)
    )
}
