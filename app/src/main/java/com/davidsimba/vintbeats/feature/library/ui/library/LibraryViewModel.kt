package com.davidsimba.vintbeats.feature.library.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.data.LibraryPreferences
import com.davidsimba.vintbeats.feature.library.domain.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    repository: TrackRepository,
    private val preferences: LibraryPreferences,
) : ViewModel() {

    val favoritesCount: StateFlow<Int> = repository.getFavoriteTracks()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val downloadsCount: StateFlow<Int> = repository.getDownloadedTracks()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isGridView: StateFlow<Boolean> = preferences.isGridView
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleGridView() {
        viewModelScope.launch {
            preferences.setGridView(!isGridView.value)
        }
    }
}
