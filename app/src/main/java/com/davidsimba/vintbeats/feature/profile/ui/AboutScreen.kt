package com.davidsimba.vintbeats.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.BuildConfig
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vintageBgGradient)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = VintageWhite,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = stringResource(R.string.profile_about),
                color = VintageWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        val context = LocalContext.current
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = context.packageManager.getApplicationIcon(context.packageName),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(22.dp)),
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Vint",
                color = VintageWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "${stringResource(R.string.about_version)} ${BuildConfig.VERSION_NAME}",
                color = VintageGrayMid,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp),
            )

            Spacer(Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.about_tagline),
                color = VintageWhiteWarm,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.about_description),
                color = VintageGrayMid,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
        }

        HorizontalDivider(
            color = VintageGrayDeep,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${stringResource(R.string.about_made_by)} David Simba",
                color = VintageGrayMid,
                fontSize = 13.sp,
            )
            Text(
                text = "© 2026",
                color = VintageGrayMid,
                fontSize = 13.sp,
            )
        }
    }
}
