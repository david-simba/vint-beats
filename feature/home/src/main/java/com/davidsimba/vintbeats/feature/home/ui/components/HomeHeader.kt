package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.components.avatar.Avatar
import com.davidsimba.vintbeats.feature.home.R
import java.time.LocalTime

@Composable
fun HomeHeader(
    userName: String,
    photoPath: String?,
    photoVersion: Long,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val greetingRes = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> R.string.home_buenos_dias
            in 12..18 -> R.string.home_buenas_tardes
            else -> R.string.home_buenas_noches
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            photoPath = photoPath,
            photoVersion = photoVersion,
            size = 44.dp,
            name = userName,
            initialSizeFraction = 17f / 44f,
            onClick = onProfileClick,
            clickLabel = stringResource(R.string.home_open_profile),
            clickable = true,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(greetingRes),
                color = Color(0xFFA1A1AA),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
            if (userName.isNotBlank()) {
                Text(
                    text = userName,
                    color = Color(0xFFF4F4F5),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
