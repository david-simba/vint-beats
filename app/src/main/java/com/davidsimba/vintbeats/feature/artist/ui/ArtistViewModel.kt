package com.davidsimba.vintbeats.feature.artist.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.data.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val repository: ArtistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val browseId: String = checkNotNull(savedStateHandle["browseId"])

    private val _uiState = MutableStateFlow<ArtistUiState>(ArtistUiState.Loading)
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()

    private val _isLoadingPlay = MutableStateFlow(false)
    val isLoadingPlay: StateFlow<Boolean> = _isLoadingPlay.asStateFlow()

    init {
        loadArtist()
    }

    private fun loadArtist() {
        viewModelScope.launch {
            runCatching { repository.getArtistDetail(browseId) }
                .onSuccess { detail ->
                    _uiState.value = ArtistUiState.Success(
                        artist = detail.artist,
                        topTracks = detail.topTracks,
                        songsBrowseId = detail.songsBrowseId,
                        albums = detail.albums
                    )
                }
                .onFailure {
                    _uiState.value = ArtistUiState.Error(it.message ?: "Failed to load artist")
                }
        }
    }

    fun loadPlayQueue(onReady: (List<Track>) -> Unit) {
        val state = _uiState.value as? ArtistUiState.Success ?: return
        if (_isLoadingPlay.value) return

        val songsBrowseId = state.songsBrowseId
        if (songsBrowseId == null) {
            onReady(state.topTracks)
            return
        }

        viewModelScope.launch {
            _isLoadingPlay.value = true
            val songs = runCatching { repository.getArtistSongs(songsBrowseId) }
                .getOrDefault(state.topTracks)
                .ifEmpty { state.topTracks }
            _isLoadingPlay.value = false
            onReady(songs)
        }
    }
}
