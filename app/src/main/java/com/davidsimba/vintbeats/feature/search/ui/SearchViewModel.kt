package com.davidsimba.vintbeats.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        _query
            .debounce(400)
            .distinctUntilChanged()
            .filter { it.length >= 2 }
            .onEach { search(it) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _query.value = query
        if (query.isEmpty()) _uiState.value = SearchUiState.Idle
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
