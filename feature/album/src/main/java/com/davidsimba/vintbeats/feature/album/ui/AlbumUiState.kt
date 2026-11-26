package com.davidsimba.vintbeats.feature.album.ui

import com.davidsimba.vintbeats.core.model.AlbumDetail

sealed interface AlbumUiState {
    data object Loading : AlbumUiState
    data class Success(val album: AlbumDetail) : AlbumUiState
    data class Error(val message: String) : AlbumUiState
}
