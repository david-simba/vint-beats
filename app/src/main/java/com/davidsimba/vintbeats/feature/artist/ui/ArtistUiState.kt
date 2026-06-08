package com.davidsimba.vintbeats.feature.artist.ui

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.data.ArtistMix
import com.davidsimba.vintbeats.feature.artist.data.ArtistRadio

sealed interface ArtistUiState {
    data object Loading : ArtistUiState
    data class Success(
        val artist: Artist,
        val topTracks: List<Track>,
        val songsBrowseId: String?,
        val albums: List<Album>,
        val mix: ArtistMix?,
        val radio: ArtistRadio?,
    ) : ArtistUiState
    data class Error(val message: String) : ArtistUiState
}
