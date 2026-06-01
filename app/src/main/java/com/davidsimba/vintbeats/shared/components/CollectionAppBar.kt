package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun rememberScrollAppBarAlpha(lazyListState: LazyListState): Float {
    val alpha by remember(lazyListState) {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > 0) return@derivedStateOf 1f
            val item = lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == 0 } ?: return@derivedStateOf 0f
            val offset = lazyListState.firstVisibleItemScrollOffset.toFloat()
            val fadeStart = item.size * 0.55f
            val fadeEnd = item.size * 0.7f
            ((offset - fadeStart) / (fadeEnd - fadeStart)).coerceIn(0f, 1f)
        }
    }
    return alpha
}

@Composable
fun CollectionAppBar(
    title: String,
    alpha: Float,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(VintageBgDark.copy(alpha = alpha))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = VintageWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                color = VintageWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .alpha(alpha)
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(alpha),
            color = VintageWhite.copy(alpha = 0.12f)
        )
    }
}
