package com.davidsimba.vintbeats.feature.library.ui.userplaylist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.domain.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.PlaylistWithTracks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserPlaylistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: PlaylistRepository,
) : ViewModel() {

    private val playlistId: Int = checkNotNull(savedStateHandle["playlistId"])

    val playlist: StateFlow<PlaylistWithTracks?> = repository
        .getPlaylistWithTracks(playlistId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
