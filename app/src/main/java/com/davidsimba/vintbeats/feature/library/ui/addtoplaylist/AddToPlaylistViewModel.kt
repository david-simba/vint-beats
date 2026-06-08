package com.davidsimba.vintbeats.feature.library.ui.addtoplaylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.domain.playlist.Playlist
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import com.davidsimba.vintbeats.shared.AddToPlaylistController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToPlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository,
) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> = playlistRepository.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds: StateFlow<Set<Int>> = _selectedIds.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun toggleSelection(playlistId: Int) {
        _selectedIds.value = _selectedIds.value.toMutableSet().apply {
            if (contains(playlistId)) remove(playlistId) else add(playlistId)
        }
    }

    fun save() {
        val track = AddToPlaylistController.pendingTrack ?: return
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        viewModelScope.launch {
            trackRepository.saveTrack(track, null)
            val savedTrack = trackRepository.getTrackByVideoId(track.id) ?: return@launch
            ids.forEach { playlistId ->
                playlistRepository.addTrackToPlaylist(playlistId, savedTrack.id)
            }
            AddToPlaylistController.pendingTrack = null
            _isSaved.value = true
        }
    }
}
