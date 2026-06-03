package com.davidsimba.vintbeats.feature.library.ui.favorites

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.youtube.YouTubeStreamService
import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import com.davidsimba.vintbeats.shared.SnackbarController
import com.davidsimba.vintbeats.shared.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val streamService: YouTubeStreamService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val favorites: StateFlow<List<SavedTrack>> = repository.getFavoriteTracks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _downloadingTrackId = MutableStateFlow<Int?>(null)
    val downloadingTrackId: StateFlow<Int?> = _downloadingTrackId.asStateFlow()

    fun removeFavorite(id: Int) {
        viewModelScope.launch { repository.removeFavorite(id) }
    }

    fun downloadTrack(track: SavedTrack) {
        if (_downloadingTrackId.value != null) return
        viewModelScope.launch {
            _downloadingTrackId.value = track.id
            SnackbarController.emit(SnackbarEvent.DownloadStarted)
            val streamUrl = streamService.getAudioStreamUrl(track.trackId)
            val audioFilePath = streamService.downloadAudio(track.trackId, streamUrl, context.filesDir)
            val fullTrack = com.davidsimba.vintbeats.core.model.Track(
                id = track.trackId,
                title = track.trackTitle,
                artist = track.trackArtist,
                albumImageUrl = track.trackThumbnailUrl,
                previewUrl = null,
                durationText = track.trackDurationText
            )
            repository.saveTrack(fullTrack, audioFilePath)
            _downloadingTrackId.value = null
            SnackbarController.emit(SnackbarEvent.DownloadSuccess)
        }
    }
}
