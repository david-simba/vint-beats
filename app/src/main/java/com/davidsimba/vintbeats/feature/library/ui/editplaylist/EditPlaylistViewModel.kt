package com.davidsimba.vintbeats.feature.library.ui.editplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlaylistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PlaylistRepository,
) : ViewModel() {

    private val playlistId: Int = checkNotNull(savedStateHandle["playlistId"])

    val tracks: StateFlow<List<SavedTrack>> = repository
        .getPlaylistWithTracks(playlistId)
        .map { it?.tracks ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeTrack(savedTrackId: Int) {
        viewModelScope.launch {
            repository.removeTrackFromPlaylist(playlistId, savedTrackId)
        }
    }

    fun reorderTracks(orderedTrackIds: List<Int>) {
        viewModelScope.launch {
            repository.reorderTracks(playlistId, orderedTrackIds)
        }
    }
}
