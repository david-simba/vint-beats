package com.davidsimba.vintbeats.feature.library.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.domain.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val repository: TrackRepository
) : ViewModel() {

    val downloads: StateFlow<List<SavedTrack>> = repository.getDownloadedTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteDownload(id: Int) {
        viewModelScope.launch { repository.deleteTrack(id) }
    }
}
