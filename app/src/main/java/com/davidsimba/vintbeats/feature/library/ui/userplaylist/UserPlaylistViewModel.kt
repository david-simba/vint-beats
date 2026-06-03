package com.davidsimba.vintbeats.feature.library.ui.userplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistWithTracks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPlaylistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PlaylistRepository,
) : ViewModel() {

    val playlistId: Int = checkNotNull(savedStateHandle["playlistId"])

    val playlist: StateFlow<PlaylistWithTracks?> = repository
        .getPlaylistWithTracks(playlistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    fun deletePlaylist() {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
            _isDeleted.value = true
        }
    }
}
