package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun LyricsCard(
    lyrics: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal =  20.dp, vertical = 28.dp)
    ) {
        Text(
            text = "Lyrics",
            color = VintageWhiteWarm,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = lyrics,
            color = VintageWhitePure.copy(alpha = 0.85f),
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}
