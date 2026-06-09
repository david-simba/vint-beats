package com.davidsimba.vintbeats.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.feature.profile.ui.components.ProfileAvatar
import com.davidsimba.vintbeats.feature.profile.ui.components.SettingNavRow
import com.davidsimba.vintbeats.feature.profile.ui.components.SettingRow
import com.davidsimba.vintbeats.feature.profile.ui.components.SettingSectionHeader
import com.davidsimba.vintbeats.shared.components.Header
import com.davidsimba.vintbeats.shared.components.VintSwitch
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm
import com.davidsimba.vintbeats.shared.theme.vintageBgGradient

@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit,
    onNavigateToAbout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val photoPath by viewModel.photoPath.collectAsStateWithLifecycle()
    val photoVersion by viewModel.photoVersion.collectAsStateWithLifecycle()
    val autoDownload by viewModel.autoDownloadFavorites.collectAsStateWithLifecycle()
    val equalizerEnabled by viewModel.equalizerEnabled.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(vintageBgGradient)
            .statusBarsPadding(),
    ) {
        Header(
            title = stringResource(R.string.profile_title),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(
                photoPath = photoPath,
                photoVersion = photoVersion,
                size = 72.dp,
            )

            Spacer(Modifier.width(20.dp))

            Column {
                Text(
                    text = name,
                    color = VintageWhiteWarm,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.profile_edit_button),
                    color = VintageGrayMid,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNavigateToEdit() },
                )
            }
        }

        SettingSectionHeader(stringResource(R.string.profile_section_settings))
        SettingRow(
            title = stringResource(R.string.profile_auto_download_title),
            subtitle = stringResource(R.string.profile_auto_download_subtitle),
            end = {
                VintSwitch(
                    checked = autoDownload,
                    onCheckedChange = { viewModel.setAutoDownloadFavorites(it) },
                )
            },
        )

        SettingRow(
            title = stringResource(R.string.profile_equalizer_title),
            subtitle = stringResource(R.string.profile_equalizer_subtitle),
            end = {
                VintSwitch(
                    checked = equalizerEnabled,
                    onCheckedChange = { viewModel.setEqualizerEnabled(it) },
                )
            },
        )

        SettingSectionHeader(stringResource(R.string.profile_section_about))
        SettingNavRow(title = stringResource(R.string.profile_support_creator))
        SettingNavRow(
            title = stringResource(R.string.profile_about),
            onClick = onNavigateToAbout,
        )
        SettingNavRow(title = stringResource(R.string.profile_terms))
    }
}
