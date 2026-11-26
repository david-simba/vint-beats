package com.davidsimba.vintbeats.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.data.library.TrackRepository
import com.davidsimba.vintbeats.core.data.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.davidsimba.vintbeats.core.data.download.DownloadProgressStore
import com.davidsimba.vintbeats.core.data.download.TrackDownloader

@HiltViewModel
class TrackActionsViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val downloader: TrackDownloader,
    private val prefs: OnboardingPreferences,
    progressStore: DownloadProgressStore,
) : ViewModel() {

    val favoriteTrackIds: StateFlow<Set<String>> = repository.getFavoriteTracks()
        .map { tracks -> tracks.map { it.trackId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val downloadedTrackIds: StateFlow<Set<String>> = repository.getDownloadedTracks()
        .map { tracks -> tracks.filter { !it.audioFilePath.isNullOrEmpty() }.map { it.trackId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val downloadingTrackIds: StateFlow<Set<String>> = repository.getDownloadedTracks()
        .map { tracks -> tracks.filter { it.isDownloading }.map { it.trackId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val downloadProgress: StateFlow<Map<String, Float>> = progressStore.progress

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            val wasAlreadyFavorite = favoriteTrackIds.value.contains(track.id)
            repository.toggleFavorite(track)
            if (!wasAlreadyFavorite && prefs.autoDownloadFavorites.first()) {
                val alreadyDownloaded = repository.getTrackByVideoId(track.id)?.audioFilePath != null
                if (!alreadyDownloaded) downloadTrack(track)
            }
        }
    }

    fun downloadTrack(track: Track) {
        if (downloadingTrackIds.value.contains(track.id)) return
        viewModelScope.launch { downloader.download(track) }
    }
}
