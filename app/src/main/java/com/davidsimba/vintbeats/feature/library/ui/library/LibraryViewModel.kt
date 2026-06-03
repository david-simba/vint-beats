package com.davidsimba.vintbeats.feature.library.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.data.LibraryPreferences
import com.davidsimba.vintbeats.feature.library.domain.playlist.Playlist
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.album.SavedAlbum
import com.davidsimba.vintbeats.feature.library.domain.album.SavedAlbumRepository
import com.davidsimba.vintbeats.feature.library.domain.artist.SavedArtist
import com.davidsimba.vintbeats.feature.library.domain.artist.SavedArtistRepository
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    repository: TrackRepository,
    playlistRepository: PlaylistRepository,
    savedAlbumRepository: SavedAlbumRepository,
    savedArtistRepository: SavedArtistRepository,
    private val preferences: LibraryPreferences,
) : ViewModel() {

    val favoritesCount: StateFlow<Int> = repository.getFavoriteTracks()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val downloadsCount: StateFlow<Int> = repository.getDownloadedTracks()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val playlists: StateFlow<List<Playlist>> = playlistRepository.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedAlbums: StateFlow<List<SavedAlbum>> = savedAlbumRepository.getSavedAlbums()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedArtists: StateFlow<List<SavedArtist>> = savedArtistRepository.getSavedArtists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isGridView: StateFlow<Boolean> = preferences.isGridView
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _filter = MutableStateFlow(LibraryFilter.User)
    val filter: StateFlow<LibraryFilter> = _filter.asStateFlow()

    fun setFilter(filter: LibraryFilter) {
        _filter.value = filter
    }

    fun toggleGridView() {
        viewModelScope.launch {
            preferences.setGridView(!isGridView.value)
        }
    }
}
