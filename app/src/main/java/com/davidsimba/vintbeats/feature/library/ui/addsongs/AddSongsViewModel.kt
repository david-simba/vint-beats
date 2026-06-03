package com.davidsimba.vintbeats.feature.library.ui.addsongs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AddSongsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val playlistId: Int = checkNotNull(savedStateHandle["playlistId"])

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Track>>(emptyList())
    val results: StateFlow<List<Track>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _addedTrackIds = MutableStateFlow<Set<String>>(emptySet())
    val addedTrackIds: StateFlow<Set<String>> = _addedTrackIds.asStateFlow()

    init {
        _query
            .debounce(400)
            .distinctUntilChanged()
            .filter { it.length >= 2 }
            .onEach { searchTracks(it) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _query.value = query
        if (query.isEmpty()) _results.value = emptyList()
    }

    fun addTrack(track: Track) {
        if (_addedTrackIds.value.contains(track.id)) return
        viewModelScope.launch {
            trackRepository.saveTrack(track, null)
            val saved = trackRepository.getTrackByVideoId(track.id) ?: return@launch
            playlistRepository.addTrackToPlaylist(playlistId, saved.id)
            _addedTrackIds.value = _addedTrackIds.value + track.id
        }
    }

    private fun searchTracks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { searchRepository.searchTracks(query) }
                .onSuccess { _results.value = it }
                .onFailure { _results.value = emptyList() }
            _isLoading.value = false
        }
    }
}
