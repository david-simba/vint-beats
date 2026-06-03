package com.davidsimba.vintbeats.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.search.domain.ExploreCategory
import com.davidsimba.vintbeats.core.youtube.BackendService
import com.davidsimba.vintbeats.core.youtube.CategoryPlaylistsResult
import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CategorySheetState {
    data object Hidden : CategorySheetState
    data class Loading(val title: String) : CategorySheetState
    data class Success(val result: CategoryPlaylistsResult) : CategorySheetState
    data object Error : CategorySheetState
}

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository,
    private val backendService: BackendService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _categories = MutableStateFlow<List<ExploreCategory>>(emptyList())
    val categories: StateFlow<List<ExploreCategory>> = _categories.asStateFlow()

    private val _categorySheet = MutableStateFlow<CategorySheetState>(CategorySheetState.Hidden)
    val categorySheet: StateFlow<CategorySheetState> = _categorySheet.asStateFlow()

    init {
        _query
            .debounce(400)
            .distinctUntilChanged()
            .filter { it.length >= 2 }
            .onEach { search(it) }
            .launchIn(viewModelScope)

        loadCategories()
    }

    fun onQueryChange(query: String) {
        _query.value = query
        if (query.isEmpty()) _uiState.value = SearchUiState.Idle
    }

    fun openCategory(category: ExploreCategory) {
        viewModelScope.launch {
            _categorySheet.value = CategorySheetState.Loading(category.title)
            val result = backendService.getCategoryPlaylists(category.id)
            _categorySheet.value = if (result != null) CategorySheetState.Success(result)
                                   else CategorySheetState.Error
        }
    }

    fun closeCategory() {
        _categorySheet.value = CategorySheetState.Hidden
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = backendService.getExploreCategories()
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            runCatching {
                coroutineScope {
                    val tracks = async { repository.searchTracks(query) }
                    val artists = async { repository.searchArtists(query) }
                    val albums = async { repository.searchAlbums(query) }
                    Triple(tracks.await(), artists.await(), albums.await())
                }
            }.onSuccess { (tracks, artists, albums) ->
                _uiState.value = SearchUiState.Success(tracks, artists, albums)
            }.onFailure {
                _uiState.value = SearchUiState.Error(it.message ?: "Error")
            }
        }
    }
}
