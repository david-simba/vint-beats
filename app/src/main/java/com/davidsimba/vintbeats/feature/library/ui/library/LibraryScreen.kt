package com.davidsimba.vintbeats.feature.library.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.components.Header
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun LibraryScreen(
    onFavoritesClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val favoritesCount by viewModel.favoritesCount.collectAsStateWithLifecycle()
    val downloadsCount by viewModel.downloadsCount.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Header(stringResource(R.string.library_title))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                LibraryCard(
                    icon = Icons.Rounded.Favorite,
                    iconTint = VintageRedLight,
                    iconBg = VintageRedLight.copy(alpha = 0.15f),
                    title = stringResource(R.string.favorites_title),
                    subtitle = if (favoritesCount == 0) stringResource(R.string.favorites_empty_short)
                               else stringResource(R.string.favorites_count, favoritesCount),
                    onClick = onFavoritesClick,
                )
            }
            item {
                LibraryCard(
                    icon = Icons.Rounded.Download,
                    iconTint = VintageWhite,
                    iconBg = VintageWhite.copy(alpha = 0.1f),
                    title = stringResource(R.string.downloads_title),
                    subtitle = if (downloadsCount == 0) stringResource(R.string.downloads_empty_short)
                               else stringResource(R.string.downloads_count, downloadsCount),
                    onClick = onDownloadsClick,
                )
            }
        }
    }
}

@Composable
private fun LibraryCard(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VintageBgDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = VintageWhiteWarm,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = VintageGrayMid,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = VintageWhite.copy(alpha = 0.3f),
            modifier = Modifier.size(20.dp)
        )
    }
}
