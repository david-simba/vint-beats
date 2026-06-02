package com.davidsimba.vintbeats.feature.library.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryCardGrid
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryCardList
import com.davidsimba.vintbeats.feature.library.ui.components.LibraryItem
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun LibraryScreen(
    onFavoritesClick: () -> Unit = {},
    onDownloadsClick: () -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val favoritesCount by viewModel.favoritesCount.collectAsStateWithLifecycle()
    val downloadsCount by viewModel.downloadsCount.collectAsStateWithLifecycle()
    val isGrid by viewModel.isGridView.collectAsStateWithLifecycle()

    val items = listOf(
        LibraryItem(
            icon = Icons.Rounded.Favorite,
            iconTint = VintageRedLight,
            iconBg = VintageRedLight.copy(alpha = 0.15f),
            title = stringResource(R.string.favorites_title),
            subtitle = if (favoritesCount == 0) stringResource(R.string.favorites_empty_short)
                       else stringResource(R.string.favorites_count, favoritesCount),
            onClick = onFavoritesClick,
        ),
        LibraryItem(
            icon = Icons.Rounded.Download,
            iconTint = VintageWhite,
            iconBg = VintageWhite.copy(alpha = 0.1f),
            title = stringResource(R.string.downloads_title),
            subtitle = if (downloadsCount == 0) stringResource(R.string.downloads_empty_short)
                       else stringResource(R.string.downloads_count, downloadsCount),
            onClick = onDownloadsClick,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.library_title),
                color = VintageWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { viewModel.toggleGridView() }) {
                Icon(
                    imageVector = if (isGrid) Icons.AutoMirrored.Rounded.ViewList else Icons.Rounded.GridView,
                    contentDescription = null,
                    tint = VintageWhite.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        if (isGrid) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    LibraryCardGrid(
                        icon = item.icon,
                        iconTint = item.iconTint,
                        iconBg = item.iconBg,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = item.onClick,
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items.size) { index ->
                    val item = items[index]
                    LibraryCardList(
                        icon = item.icon,
                        iconTint = item.iconTint,
                        iconBg = item.iconBg,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = item.onClick,
                    )
                }
            }
        }
    }
}
