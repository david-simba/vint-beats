package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure

@Composable
fun TrackInfo(
    title: String,
    artist: String,
    modifier: Modifier = Modifier,
    titleSize: TextUnit = 13.sp,
    artistSize: TextUnit = 11.sp,
    titleWeight: FontWeight = FontWeight.SemiBold,
    titleColor: Color = VintageWhitePure,
    artistColor: Color = VintageGrayMid,
    textAlign: TextAlign = TextAlign.Start
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            color = titleColor,
            fontSize = titleSize,
            lineHeight = titleSize,
            fontWeight = titleWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign
        )
        Text(
            text = artist,
            color = artistColor,
            fontSize = artistSize,
            lineHeight = artistSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign
        )
    }
}
