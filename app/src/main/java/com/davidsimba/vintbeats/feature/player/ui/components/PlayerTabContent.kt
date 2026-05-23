package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.components.VintCard
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun LyricsCard(
    lyrics: String?,
    modifier: Modifier = Modifier
) {
    VintCard(modifier = modifier) {
        Text(
            text = "Lyrics",
            color = VintageWhiteWarm,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        HorizontalDivider(
            color = VintageGrayDeep.copy(alpha = 1f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(bottom = 14.dp)
        )
        Text(
            text = lyrics ?: "No lyrics available for this song.",
            color = if (lyrics != null) VintageWhitePure.copy(alpha = 0.85f)
                    else VintageWhitePure.copy(alpha = 0.4f),
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun PlayerEffectsCard(modifier: Modifier = Modifier) {
    VintCard(modifier = modifier) {
        Text(
            text = "Player",
            color = VintageWhiteWarm,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Effects, timers, and more coming soon.",
            color = VintageWhitePure.copy(alpha = 0.5f),
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}

