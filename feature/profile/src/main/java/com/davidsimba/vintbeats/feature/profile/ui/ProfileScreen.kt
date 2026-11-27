package com.davidsimba.vintbeats.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.feature.profile.R
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileAvatar
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileCard
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileEditPill
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileNavRow
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileRowDivider
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileSectionTitle
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileSupportCard
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileToggleRow

@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToTerms: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val photoPath by viewModel.photoPath.collectAsStateWithLifecycle()
    val photoVersion by viewModel.photoVersion.collectAsStateWithLifecycle()
    val autoDownload by viewModel.autoDownloadFavorites.collectAsStateWithLifecycle()
    val equalizerEnabled by viewModel.equalizerEnabled.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val versionName = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrNull()
            .orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E10))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 120.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            color = Color(0xFFF4F4F5),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 16.dp),
        )

        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(
                photoPath = photoPath,
                photoVersion = photoVersion,
                size = 72.dp,
                name = name,
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color(0xFFF4F4F5),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                ProfileEditPill(
                    text = stringResource(R.string.profile_edit_button),
                    onClick = onNavigateToEdit,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        ProfileSupportCard(
            title = stringResource(R.string.profile_support_creator),
            subtitle = stringResource(R.string.profile_support_creator_sub),
            onClick = { uriHandler.openUri("https://ko-fi.com/davidsimbaec") },
        )

        Spacer(Modifier.height(24.dp))

        ProfileSectionTitle(stringResource(R.string.profile_section_settings))
        ProfileCard {
            ProfileToggleRow(
                icon = Icons.Rounded.Download,
                title = stringResource(R.string.profile_auto_download_title),
                subtitle = stringResource(R.string.profile_auto_download_subtitle),
                checked = autoDownload,
                onCheckedChange = viewModel::setAutoDownloadFavorites,
            )
            ProfileRowDivider()
            ProfileToggleRow(
                icon = Icons.Rounded.Tune,
                title = stringResource(R.string.profile_equalizer_title),
                subtitle = stringResource(R.string.profile_equalizer_subtitle),
                checked = equalizerEnabled,
                onCheckedChange = viewModel::setEqualizerEnabled,
            )
        }

        Spacer(Modifier.height(24.dp))

        ProfileSectionTitle(stringResource(R.string.profile_section_about))
        ProfileCard {
            ProfileNavRow(
                title = stringResource(R.string.profile_about),
                trailingText = versionName.takeIf { it.isNotBlank() }?.let { "v$it" },
                onClick = onNavigateToAbout,
            )
            ProfileRowDivider()
            ProfileNavRow(
                title = stringResource(R.string.profile_terms),
                onClick = onNavigateToTerms,
            )
        }
    }
}
