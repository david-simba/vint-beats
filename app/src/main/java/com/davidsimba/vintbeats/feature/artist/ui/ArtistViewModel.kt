package com.davidsimba.vintbeats.feature.artist.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.artist.data.ArtistRepository
import com.davidsimba.vintbeats.feature.library.domain.artist.SavedArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    private val repository: ArtistRepository,
    private val savedArtistRepository: SavedArtistRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val browseId: String = checkNotNull(savedStateHandle["browseId"])

    private val _uiState = MutableStateFlow<ArtistUiState>(ArtistUiState.Loading)
    val uiState: StateFlow<ArtistUiState> = _uiState.asStateFlow()

    private val _isLoadingPlay = MutableStateFlow(false)
    val isLoadingPlay: StateFlow<Boolean> = _isLoadingPlay.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

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
                        albums = detail.albums,
                        mix = detail.mix,
                        radio = detail.radio,
                    )
                    _isSaved.value = savedArtistRepository.isSaved(detail.artist.id)
                }
                .onFailure {
                    _uiState.value = ArtistUiState.Error(it.message ?: "Failed to load artist")
                }
        }
    }

    fun toggleSave() {
        val state = _uiState.value as? ArtistUiState.Success ?: return
        viewModelScope.launch {
            if (_isSaved.value) {
                savedArtistRepository.unsaveArtist(state.artist.id)
            } else {
                savedArtistRepository.saveArtist(
                    artistId = state.artist.id,
                    name = state.artist.name,
                    thumbnailUrl = state.artist.thumbnailUrl
                )
            }
            _isSaved.value = !_isSaved.value
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
