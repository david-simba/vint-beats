package com.davidsimba.vintbeats.feature.player.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.davidsimba.vintbeats.core.youtube.YouTubeMusicService
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteConfig
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteRepository
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette
import com.davidsimba.vintbeats.feature.search.domain.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val repository: CassetteRepository,
    private val youTubeMusic: YouTubeMusicService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val player = ExoPlayer.Builder(context).build()
    private var currentStreamUrl: String? = null

    private val _currentCassette = MutableStateFlow<SavedCassette?>(null)
    val currentCassette: StateFlow<SavedCassette?> = _currentCassette.asStateFlow()

    private val _unsavedTrack = MutableStateFlow<Track?>(null)
    val unsavedTrack: StateFlow<Track?> = _unsavedTrack.asStateFlow()

    private val _isSaved = MutableStateFlow(true)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private var progressJob: Job? = null

    fun playTrack(track: Track) {
        if (_unsavedTrack.value?.id == track.id && player.isPlaying) return
        _unsavedTrack.value = track
        _currentCassette.value = null
        _isSaved.value = false
        currentStreamUrl = null
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            val streamUrl = youTubeMusic.getAudioStreamUrl(track.id) ?: run {
                Log.e(TAG, "No stream for ${track.id}")
                _playerState.value = PlayerState.Error("Stream not available")
                return@launch
            }
            currentStreamUrl = streamUrl
            withContext(Dispatchers.Main) {
                player.stop()
                player.setMediaItem(MediaItem.fromUri(streamUrl))
                player.prepare()
                player.play()
                _playerState.value = PlayerState.Playing
                startProgressUpdates()
            }
        }
    }

    fun play(cassetteId: Int) {
        if (_currentCassette.value?.id == cassetteId && player.isPlaying) return
        _unsavedTrack.value = null
        _isSaved.value = true
        currentStreamUrl = null
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            val cassette = repository.getCassette(cassetteId) ?: run {
                _playerState.value = PlayerState.Error("Not found")
                return@launch
            }
            _currentCassette.value = cassette

            val uri = if (!cassette.audioFilePath.isNullOrEmpty() && File(cassette.audioFilePath).exists()) {
                Log.d(TAG, "Playing local file: ${cassette.audioFilePath}")
                Uri.fromFile(File(cassette.audioFilePath))
            } else {
                Log.d(TAG, "Streaming ${cassette.trackId}")
                val streamUrl = youTubeMusic.getAudioStreamUrl(cassette.trackId) ?: run {
                    _playerState.value = PlayerState.Error("Audio not available")
                    return@launch
                }
                currentStreamUrl = streamUrl
                Uri.parse(streamUrl)
            }

            withContext(Dispatchers.Main) {
                player.stop()
                player.setMediaItem(MediaItem.fromUri(uri))
                player.prepare()
                player.play()
                _playerState.value = PlayerState.Playing
                startProgressUpdates()
            }
        }
    }

    fun saveCassette(config: CassetteConfig, onSaved: () -> Unit) {
        val track = _unsavedTrack.value ?: return
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            val streamUrl = currentStreamUrl ?: youTubeMusic.getAudioStreamUrl(track.id)
            val audioFilePath = streamUrl?.let {
                youTubeMusic.downloadAudio(track.id, it, context.filesDir)
            }
            repository.saveCassette(config, audioFilePath)
            _isSaved.value = true
            _playerState.value = PlayerState.Idle
            withContext(Dispatchers.Main) { onSaved() }
        }
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
            _playerState.value = PlayerState.Idle
            progressJob?.cancel()
        } else {
            player.play()
            _playerState.value = PlayerState.Playing
            startProgressUpdates()
        }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _positionMs.value = positionMs
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                _positionMs.value = player.currentPosition
                _durationMs.value = player.duration.takeIf { it > 0 } ?: 0L
                delay(500)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        player.release()
    }

    companion object {
        private const val TAG = "PlaybackViewModel"
    }
}
