package com.davidsimba.vintbeats.feature.library.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.SavedTrack
import com.davidsimba.vintbeats.core.data.library.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: TrackRepository
) : ViewModel() {

    val favorites: StateFlow<List<SavedTrack>> = repository.getFavoriteTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
