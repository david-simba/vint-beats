package com.davidsimba.vintbeats.feature.search.ui

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track

sealed interface SearchUiState {
    data object Idle: SearchUiState
    data object Loading: SearchUiState
    data class Success(val tracks: List<Track>, val artists: List<Artist>): SearchUiState
    data class Error(val message: String): SearchUiState
}
