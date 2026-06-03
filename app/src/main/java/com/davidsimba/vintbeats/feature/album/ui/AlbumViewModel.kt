package com.davidsimba.vintbeats.feature.album.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.album.data.AlbumRepository
import com.davidsimba.vintbeats.feature.library.domain.album.SavedAlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val repository: AlbumRepository,
    private val savedAlbumRepository: SavedAlbumRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val browseId: String = checkNotNull(savedStateHandle["browseId"])

    private val _uiState = MutableStateFlow<AlbumUiState>(AlbumUiState.Loading)
    val uiState: StateFlow<AlbumUiState> = _uiState.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    init {
        loadAlbum()
    }

    private fun loadAlbum() {
        viewModelScope.launch {
            runCatching { repository.getAlbumDetail(browseId) }
                .onSuccess { album ->
                    _uiState.value = AlbumUiState.Success(album)
                    _isSaved.value = savedAlbumRepository.isSaved(album.id)
                }
                .onFailure { _uiState.value = AlbumUiState.Error(it.message ?: "Failed to load album") }
        }
    }

    fun toggleSave() {
        val album = (_uiState.value as? AlbumUiState.Success)?.album ?: return
        viewModelScope.launch {
            if (_isSaved.value) {
                savedAlbumRepository.unsaveAlbum(album.id)
            } else {
                savedAlbumRepository.saveAlbum(
                    albumId = album.id,
                    title = album.title,
                    artist = album.artist,
                    thumbnailUrl = album.thumbnailUrl
                )
            }
            _isSaved.value = !_isSaved.value
        }
    }

    fun tracks(): List<Track> =
        (_uiState.value as? AlbumUiState.Success)?.album?.tracks ?: emptyList()
}
