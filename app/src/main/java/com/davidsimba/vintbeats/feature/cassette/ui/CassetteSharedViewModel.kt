package com.davidsimba.vintbeats.feature.cassette.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.compose.ui.graphics.Color
import com.davidsimba.vintbeats.core.youtube.YouTubeMusicService
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteConfig
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteRepository
import com.davidsimba.vintbeats.feature.search.domain.Track
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface PlayerState {
    data object Idle : PlayerState
    data object Loading : PlayerState
    data object Playing : PlayerState
    data class Error(val message: String) : PlayerState
}

@HiltViewModel
class CassetteSharedViewModel @Inject constructor(
    private val youTubeMusic: YouTubeMusicService,
    private val repository: CassetteRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val player = ExoPlayer.Builder(context).build()

    private val _cassetteConfig = MutableStateFlow(
        CassetteConfig(
            track = Track("", "", "", null, null),
            cassetteColor = VintageBlackMid,
            lineColor = VintageRedLight,
            isRainbow = true
        )
    )
    val cassetteConfig: StateFlow<CassetteConfig> = _cassetteConfig

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    fun selectTrack(track: Track) {
        _cassetteConfig.value = _cassetteConfig.value.copy(track = track)
        startPlayback(track.id)
    }

    fun onScreenResume() {
        when {
            _playerState.value is PlayerState.Error -> {
                val videoId = _cassetteConfig.value.track.id
                if (videoId.isNotEmpty()) startPlayback(videoId)
            }
            player.mediaItemCount > 0 && _playerState.value !is PlayerState.Loading -> {
                player.play()
                _playerState.value = PlayerState.Playing
            }
        }
    }

    fun onScreenPause() {
        player.pause()
    }

    fun updateCassetteColor(color: Color) {
        _cassetteConfig.value = _cassetteConfig.value.copy(cassetteColor = color)
    }

    fun updateLineColor(color: Color) {
        _cassetteConfig.value = _cassetteConfig.value.copy(lineColor = color)
    }

    fun updateStyle(isRainbow: Boolean) {
        _cassetteConfig.value = _cassetteConfig.value.copy(isRainbow = isRainbow)
    }

    fun saveCassette(onSaved: () -> Unit) {
        viewModelScope.launch {
            val config = _cassetteConfig.value
            if (config.track.id.isEmpty()) return@launch
            repository.saveCassette(config)
            player.pause()
            _playerState.value = PlayerState.Idle
            withContext(Dispatchers.Main) { onSaved() }
        }
    }

    private fun startPlayback(videoId: String) {
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            Log.d(TAG, "Fetching stream for videoId=$videoId")

            val url = youTubeMusic.getAudioStreamUrl(videoId)
            if (url == null) {
                Log.e(TAG, "No stream URL returned for videoId=$videoId")
                _playerState.value = PlayerState.Error("Stream not available")
                return@launch
            }

            Log.d(TAG, "Stream URL obtained, starting ExoPlayer")
            withContext(Dispatchers.Main) {
                player.stop()
                player.setMediaItem(MediaItem.fromUri(url))
                player.prepare()
                player.play()
                _playerState.value = PlayerState.Playing
                Log.d(TAG, "ExoPlayer playing — isPlaying=${player.isPlaying}")
            }
        }
    }

    companion object {
        private const val TAG = "CassetteViewModel"
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
