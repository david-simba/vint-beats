package com.davidsimba.vintbeats.feature.artist.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        loadArtist()
    }

    private fun loadArtist() {
        viewModelScope.launch {
            runCatching { repository.getArtistDetail(browseId) }
                .onSuccess { (artist, tracks) ->
                    _uiState.value = ArtistUiState.Success(artist, tracks)
                }
                .onFailure {
                    _uiState.value = ArtistUiState.Error(it.message ?: "Failed to load artist")
                }
        }
    }
}
