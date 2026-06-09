package com.davidsimba.vintbeats.shared

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.youtube.YouTubeStreamService
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import com.davidsimba.vintbeats.feature.onboarding.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackActionsViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val streamService: YouTubeStreamService,
    private val prefs: OnboardingPreferences,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val favoriteTrackIds: StateFlow<Set<String>> = repository.getFavoriteTracks()
        .map { tracks -> tracks.map { it.trackId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val downloadedTrackIds: StateFlow<Set<String>> = repository.getDownloadedTracks()
        .map { tracks -> tracks.filter { !it.audioFilePath.isNullOrEmpty() }.map { it.trackId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _downloadingTrackId = MutableStateFlow<String?>(null)
    val downloadingTrackId: StateFlow<String?> = _downloadingTrackId.asStateFlow()

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
        if (_downloadingTrackId.value != null) return
        viewModelScope.launch {
            _downloadingTrackId.value = track.id
            SnackbarController.emit(SnackbarEvent.DownloadStarted)
            val streamUrl = streamService.getAudioStreamUrl(track.id)
            val audioFilePath = streamService.downloadAudio(track.id, streamUrl, context.filesDir)
            repository.saveTrack(track, audioFilePath)
            _downloadingTrackId.value = null
            SnackbarController.emit(SnackbarEvent.DownloadSuccess)
        }
    }
}
