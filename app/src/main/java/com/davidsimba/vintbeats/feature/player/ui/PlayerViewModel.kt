package com.davidsimba.vintbeats.feature.player.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.davidsimba.vintbeats.core.youtube.YouTubeMusicService
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteRepository
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette
import com.davidsimba.vintbeats.feature.cassette.ui.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CassetteRepository,
    private val youTubeMusic: YouTubeMusicService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val cassetteId: Int = checkNotNull(savedStateHandle["cassetteId"])
    private val player = ExoPlayer.Builder(context).build()

    private val _cassette = MutableStateFlow<SavedCassette?>(null)
    val cassette: StateFlow<SavedCassette?> = _cassette.asStateFlow()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Loading)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private var progressJob: Job? = null

    init {
        loadAndPlay()
    }

    private fun loadAndPlay() {
        viewModelScope.launch {
            val cassette = repository.getCassette(cassetteId) ?: run {
                _playerState.value = PlayerState.Error("Cassette not found")
                return@launch
            }
            _cassette.value = cassette

            val uri = if (!cassette.audioFilePath.isNullOrEmpty() && File(cassette.audioFilePath).exists()) {
                Log.d(TAG, "Playing from local file: ${cassette.audioFilePath}")
                Uri.fromFile(File(cassette.audioFilePath))
            } else {
                Log.d(TAG, "Local file missing, streaming for ${cassette.trackId}")
                val streamUrl = youTubeMusic.getAudioStreamUrl(cassette.trackId)
                if (streamUrl != null) Uri.parse(streamUrl) else null
            }

            if (uri == null) {
                _playerState.value = PlayerState.Error("Audio not available")
                return@launch
            }

            withContext(Dispatchers.Main) {
                player.setMediaItem(MediaItem.fromUri(uri))
                player.prepare()
                player.play()
                _playerState.value = PlayerState.Playing
                startProgressUpdates()
            }
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
                val dur = player.duration.takeIf { it > 0 } ?: 0L
                _durationMs.value = dur
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
        private const val TAG = "PlayerViewModel"
    }
}
