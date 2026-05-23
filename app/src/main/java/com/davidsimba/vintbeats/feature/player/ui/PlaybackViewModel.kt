package com.davidsimba.vintbeats.feature.player.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.davidsimba.vintbeats.core.youtube.YouTubeLyricsService
import com.davidsimba.vintbeats.core.youtube.YouTubeQueueService
import com.davidsimba.vintbeats.core.youtube.YouTubeStreamService
import com.davidsimba.vintbeats.feature.library.domain.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.TrackRepository
import com.davidsimba.vintbeats.core.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val repository: TrackRepository,
    private val streamService: YouTubeStreamService,
    private val queueService: YouTubeQueueService,
    private val lyricsService: YouTubeLyricsService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val player = ExoPlayer.Builder(context).build()
    private var currentStreamUrl: String? = null

    private val _currentSavedTrack = MutableStateFlow<SavedTrack?>(null)
    val currentSavedTrack: StateFlow<SavedTrack?> = _currentSavedTrack.asStateFlow()

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

    private val _lyrics = MutableStateFlow<String?>(null)
    val lyrics: StateFlow<String?> = _lyrics.asStateFlow()

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private var progressJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    viewModelScope.launch(Dispatchers.Main) { playNextInQueue() }
                }
            }
        })
    }

    fun playTrack(track: Track, newQueue: List<Track>? = null) {
        Log.d(TAG, "playTrack: ${track.id} '${track.title}'")
        if (_unsavedTrack.value?.id == track.id && player.isPlaying) {
            Log.d(TAG, "playTrack: skipped (already playing)")
            return
        }
        _unsavedTrack.value = track
        _currentSavedTrack.value = null
        _isSaved.value = false
        currentStreamUrl = null
        _lyrics.value = null
        _queue.value = newQueue ?: emptyList()
        viewModelScope.launch { _lyrics.value = lyricsService.getLyrics(track.id) }
        if (newQueue == null) {
            viewModelScope.launch { _queue.value = queueService.getUpNextTracks(track.id) }
        }
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            Log.d(TAG, "playTrack: fetching stream for ${track.id}")
            val streamUrl = streamService.getAudioStreamUrl(track.id) ?: run {
                Log.e(TAG, "playTrack: no stream for ${track.id}")
                _playerState.value = PlayerState.Error("Stream not available")
                return@launch
            }
            Log.d(TAG, "playTrack: got stream, starting playback for ${track.id}")
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

    fun play(savedTrackId: Int) {
        if (_currentSavedTrack.value?.id == savedTrackId && player.isPlaying) return
        _unsavedTrack.value = null
        _isSaved.value = true
        currentStreamUrl = null
        _lyrics.value = null
        _queue.value = emptyList()
        viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            val saved = repository.getTrack(savedTrackId) ?: run {
                _playerState.value = PlayerState.Error("Not found")
                return@launch
            }
            _currentSavedTrack.value = saved
            viewModelScope.launch { _lyrics.value = lyricsService.getLyrics(saved.trackId) }
            viewModelScope.launch {
                _queue.value = repository.getAllTracks().first()
                    .filter { it.id != savedTrackId }
                    .map {
                        Track(
                            id = it.trackId, title = it.trackTitle, artist = it.trackArtist,
                            albumImageUrl = it.trackThumbnailUrl, previewUrl = null,
                            durationText = it.trackDurationText
                        )
                    }
            }

            val uri = if (!saved.audioFilePath.isNullOrEmpty() && File(saved.audioFilePath).exists()) {
                Log.d(TAG, "Playing local file: ${saved.audioFilePath}")
                Uri.fromFile(File(saved.audioFilePath))
            } else {
                Log.d(TAG, "Streaming ${saved.trackId}")
                val streamUrl = streamService.getAudioStreamUrl(saved.trackId) ?: run {
                    _playerState.value = PlayerState.Error("Audio not available")
                    return@launch
                }
                currentStreamUrl = streamUrl
                streamUrl.toUri()
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

    fun reorderQueue(from: Int, to: Int) {
        _queue.value = _queue.value.toMutableList().apply { add(to, removeAt(from)) }
    }

    fun skipToQueueTrack(track: Track) {
        val index = _queue.value.indexOf(track)
        if (index >= 0) {
            playTrack(track, newQueue = _queue.value.drop(index + 1))
        }
    }

    fun downloadCurrentTrack() {
        val track = _unsavedTrack.value ?: return
        if (_isDownloading.value) return
        viewModelScope.launch {
            _isDownloading.value = true
            val streamUrl = currentStreamUrl ?: streamService.getAudioStreamUrl(track.id)
            val audioFilePath = streamUrl?.let {
                streamService.downloadAudio(track.id, it, context.filesDir)
            }
            repository.saveTrack(track, audioFilePath)
            val saved = repository.getAllTracks().first().find { it.trackId == track.id }
            _currentSavedTrack.value = saved
            _unsavedTrack.value = null
            _isSaved.value = true
            _isDownloading.value = false
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

    private fun playNextInQueue() {
        val queue = _queue.value
        Log.d(TAG, "playNextInQueue: queue size=${queue.size}, playerState=${_playerState.value}")
        if (queue.isEmpty()) {
            Log.d(TAG, "playNextInQueue: queue exhausted → Idle")
            _playerState.value = PlayerState.Idle
            progressJob?.cancel()
            return
        }
        val next = queue.first()
        Log.d(TAG, "playNextInQueue: playing next → ${next.id} '${next.title}'")
        playTrack(next, newQueue = queue.drop(1))
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
