package com.davidsimba.vintbeats.feature.search.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.model.PlaylistSummary
import com.davidsimba.vintbeats.feature.search.ui.components.CategorySheetContent
import com.davidsimba.vintbeats.feature.search.ui.components.SearchField
import com.davidsimba.vintbeats.feature.search.ui.components.exploreGrid
import com.davidsimba.vintbeats.shared.components.Header
import com.davidsimba.vintbeats.shared.theme.VintageBgDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onSearchTap: () -> Unit,
    onPlaylistSelected: (PlaylistSummary) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categorySheet by viewModel.categorySheet.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (categorySheet !is CategorySheetState.Hidden) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeCategory,
            sheetState = sheetState,
            containerColor = VintageBgDark
        ) {
            CategorySheetContent(
                state = categorySheet,
                onPlaylistSelected = { playlist ->
                    viewModel.closeCategory()
                    onPlaylistSelected(playlist)
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
            item { Header(stringResource(R.string.search_title)) }
            stickyHeader {
                SearchField(
                    query = "",
                    onQueryChange = {},
                    onClick = onSearchTap
                )
            }
            exploreGrid(
                categories = categories,
                onCategoryClick = viewModel::openCategory
            )
        }
    }
}
