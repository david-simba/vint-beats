package com.davidsimba.vintbeats.feature.album.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.album.data.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: AlbumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val browseId: String = checkNotNull(savedStateHandle["browseId"])

    private val _uiState = MutableStateFlow<AlbumUiState>(AlbumUiState.Loading)
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    init {
        loadAlbum()
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            runCatching { repository.getAlbumDetail(browseId) }
                .onSuccess { _uiState.value = AlbumUiState.Success(it) }
                .onFailure { _uiState.value = AlbumUiState.Error(it.message ?: "Failed to load album") }
        }
    }

    fun tracks(): List<Track> =
        (_uiState.value as? AlbumUiState.Success)?.album?.tracks ?: emptyList()
}
