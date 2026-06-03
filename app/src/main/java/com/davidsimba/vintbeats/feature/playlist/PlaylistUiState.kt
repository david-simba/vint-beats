package com.davidsimba.vintbeats.feature.playlist

import com.davidsimba.vintbeats.feature.playlist.PlaylistDetail

sealed interface PlaylistUiState {
    data object Loading : PlaylistUiState
    data class Success(val detail: PlaylistDetail) : PlaylistUiState
    data class Error(val message: String) : PlaylistUiState
}
