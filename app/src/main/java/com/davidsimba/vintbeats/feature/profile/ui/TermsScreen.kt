package com.davidsimba.vintbeats.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient

@Composable
fun TermsScreen(onBack: () -> Unit) {
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
                text = stringResource(R.string.terms_title),
                color = VintageWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.terms_last_updated),
                color = VintageGrayMid,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 24.dp),
            )

            TermsSection(
                title = stringResource(R.string.terms_acceptance_title),
                body = stringResource(R.string.terms_acceptance_body),
            )
            TermsSection(
                title = stringResource(R.string.terms_service_title),
                body = stringResource(R.string.terms_service_body),
            )
            TermsSection(
                title = stringResource(R.string.terms_content_title),
                body = stringResource(R.string.terms_content_body),
            )
            TermsSection(
                title = stringResource(R.string.terms_data_title),
                body = stringResource(R.string.terms_data_body),
            )
            TermsSection(
                title = stringResource(R.string.terms_liability_title),
                body = stringResource(R.string.terms_liability_body),
            )
            TermsSection(
                title = stringResource(R.string.terms_changes_title),
                body = stringResource(R.string.terms_changes_body),
            )
            TermsSection(
                title = stringResource(R.string.terms_contact_title),
                body = stringResource(R.string.terms_contact_body),
            )
        }
    }
}

@Composable
private fun TermsSection(title: String, body: String) {
    Text(
        text = title,
        color = VintageWhiteWarm,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(6.dp))
    Text(
        text = body,
        color = VintageGrayMid,
        fontSize = 13.sp,
        lineHeight = 20.sp,
    )
    Spacer(Modifier.height(20.dp))
}
