package com.davidsimba.vintbeats.feature.search.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.core.model.PlaylistSummary
import com.davidsimba.vintbeats.feature.search.ui.components.CategorySheetContent
import com.davidsimba.vintbeats.feature.search.ui.components.SearchInput
import com.davidsimba.vintbeats.feature.search.ui.components.exploreGrid
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.TextoPrincipal
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBgDark
import com.davidsimba.vintbeats.feature.search.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    Box(modifier = Modifier.fillMaxSize().background(Fondo).statusBarsPadding()) {
        // Padding inferior amplio para que el mini player y la barra no tapen la última fila.
        LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
            item {
                Text(
                    text = stringResource(R.string.search_title),
                    color = TextoPrincipal,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp)
                )
            }
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Fondo)
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    SearchInput(
                        query = "",
                        onQueryChange = {},
                        onClick = onSearchTap,
                    )
                }
            }
            exploreGrid(
                categories = categories,
                onCategoryClick = viewModel::openCategory
            )
        }
    }
}
