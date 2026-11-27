package com.davidsimba.vintbeats.feature.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import com.davidsimba.vintbeats.feature.search.R

@Composable
fun ExploreCard(
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.search_category_description, title)
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .semantics { contentDescription = description }
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        // Cuadrado decorativo, solo adorno.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 14.dp, y = 14.dp)
                .size(56.dp)
                .rotate(20f)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.8f)
                .padding(12.dp)
        )
    }
}

@Composable
fun ExploreCardSkeleton(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Superficie)
    )
}
