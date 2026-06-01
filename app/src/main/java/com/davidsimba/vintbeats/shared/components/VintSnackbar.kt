package com.davidsimba.vintbeats.shared.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageWhite

enum class SnackbarPosition { TOP, BOTTOM }

@Composable
fun VintSnackbar(
    message: String,
    icon: ImageVector,
    visible: Boolean,
    modifier: Modifier = Modifier,
    position: SnackbarPosition = SnackbarPosition.BOTTOM
) {
    val enterSlide = if (position == SnackbarPosition.TOP)
        slideInVertically(initialOffsetY = { -it })
    else
        slideInVertically(initialOffsetY = { it })

    val exitSlide = if (position == SnackbarPosition.TOP)
        slideOutVertically(targetOffsetY = { -it })
    else
        slideOutVertically(targetOffsetY = { it })

    AnimatedVisibility(
        visible = visible,
        enter = enterSlide + fadeIn(),
        exit = exitSlide + fadeOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .then(if (position == SnackbarPosition.TOP) Modifier.statusBarsPadding() else Modifier.navigationBarsPadding())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(
                    color = VintageBgDark.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VintageWhite,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = message,
                color = VintageWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
