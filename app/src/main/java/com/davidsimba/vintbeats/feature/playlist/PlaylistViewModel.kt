package com.davidsimba.vintbeats.feature.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.youtube.BackendService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val backendService: BackendService
) : ViewModel() {

    private val playlistId: String = checkNotNull(savedStateHandle["playlistId"])
    private val navThumbnailUrl: String? = savedStateHandle["thumbnailUrl"]
    private val artistId: String? = savedStateHandle["artistId"]
    private val artistName: String? = savedStateHandle["artistName"]

    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = PlaylistUiState.Loading
            val detail = if (artistId != null && artistName != null) {
                backendService.getHomeMix(artistId, artistName)
            } else {
                backendService.getPlaylistDetail(playlistId)
            }
            _uiState.value = if (detail != null) {
                val overridden = detail.copy(
                    title = if (artistName != null) "This Is $artistName" else detail.title,
                    thumbnailUrl = navThumbnailUrl ?: detail.thumbnailUrl
                )
                PlaylistUiState.Success(overridden)
            } else PlaylistUiState.Error("Playlist not found")
        }
    }
}
