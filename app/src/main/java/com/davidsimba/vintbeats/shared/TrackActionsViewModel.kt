package com.davidsimba.vintbeats.shared

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.youtube.YouTubeStreamService
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import com.davidsimba.vintbeats.feature.onboarding.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val progressStore: DownloadProgressStore,
    @ApplicationContext private val context: Context
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
        viewModelScope.launch {
            try {
                repository.startDownload(track)
                SnackbarController.emit(SnackbarEvent.DownloadStarted)
                val streamUrl = streamService.getAudioStreamUrl(track.id)
                val audioFilePath = streamService.downloadAudio(
                    videoId = track.id,
                    streamUrl = streamUrl,
                    destDir = context.filesDir,
                    onProgress = { progressStore.set(track.id, it) }
                )
                progressStore.remove(track.id)
                repository.finishDownload(track.id, audioFilePath)
                if (audioFilePath == null) {
                    SnackbarController.emit(SnackbarEvent.DownloadError)
                }
            } catch (e: Exception) {
                Log.e("TrackActionsVM", "[${track.id}] download failed: ${e::class.simpleName}: ${e.message}", e)
                progressStore.remove(track.id)
                repository.finishDownload(track.id, null)
                SnackbarController.emit(SnackbarEvent.DownloadError)
            }
        }
    }
}
