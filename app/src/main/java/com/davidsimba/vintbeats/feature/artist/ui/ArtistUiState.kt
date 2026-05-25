package com.davidsimba.vintbeats.feature.artist.ui

import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track

sealed interface ArtistUiState {
    data object Loading : ArtistUiState
    data class Success(val artist: Artist, val topTracks: List<Track>) : ArtistUiState
    data class Error(val message: String) : ArtistUiState
}
