package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.core.designsystem.theme.TextoSecundario
import com.davidsimba.vintbeats.feature.home.R
import java.time.LocalTime

@Composable
fun HomeHeader(
    userName: String,
    modifier: Modifier = Modifier,
) {
    val greetingRes = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> R.string.home_buenos_dias
            in 12..18 -> R.string.home_buenas_tardes
            else -> R.string.home_buenas_noches
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(greetingRes),
            color = TextoPrincipal,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        if (userName.isNotBlank()) {
            Text(
                text = userName,
                color = TextoSecundario,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
