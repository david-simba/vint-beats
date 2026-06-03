package com.davidsimba.vintbeats.feature.player.ui

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.shared.SnackbarController
import com.davidsimba.vintbeats.shared.SnackbarEvent
import com.davidsimba.vintbeats.core.youtube.LrcLibService
import com.davidsimba.vintbeats.core.youtube.YouTubeQueueService
import com.davidsimba.vintbeats.core.youtube.YouTubeStreamService
import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val repository: TrackRepository,
    private val streamService: YouTubeStreamService,
    private val backendService: com.davidsimba.vintbeats.core.youtube.BackendService,
    private val lrcLibService: LrcLibService,
    private val queueService: YouTubeQueueService,
    private val sessionPreferences: PlayerSessionPreferences,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>
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

    private val _syncedLyrics = MutableStateFlow<List<LyricLine>>(emptyList())
    val syncedLyrics: StateFlow<List<LyricLine>> = _syncedLyrics.asStateFlow()

    private val _isLoadingLyrics = MutableStateFlow(false)
    val isLoadingLyrics: StateFlow<Boolean> = _isLoadingLyrics.asStateFlow()

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _history = MutableStateFlow<List<Track>>(emptyList())
    val history: StateFlow<List<Track>> = _history.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val lyricsCache = mutableMapOf<String, List<LyricLine>>()
    private var prefetchJob: Job? = null
    private var progressJob: Job? = null
    private var playbackJob: Job? = null
    private var queueFetchJob: Job? = null

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get().also { setupControllerListener(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to connect to PlaybackService", e)
            }
        }, ContextCompat.getMainExecutor(context))

        // Receive next/previous triggered by notification buttons or track end.
        viewModelScope.launch {
            PlaybackEventBus.events.collect { event ->
                when (event) {
                    PlaybackEvent.SkipNext -> skipToNext()
                    PlaybackEvent.SkipPrevious -> skipToPrevious()
                }
            }
        }

        restoreLastTrack()

        // Keep currentSavedTrack in sync with DB (e.g. downloaded from another screen)
        viewModelScope.launch {
            repository.getAllTracks().collect { tracks ->
                val current = _currentSavedTrack.value ?: return@collect
                val updated = tracks.find { it.id == current.id }
                if (updated != null && updated != current) {
                    _currentSavedTrack.value = updated
                }
            }
        }
    }

    private fun setupControllerListener(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    _playerState.value = PlayerState.Playing
                    startProgressUpdates()
                } else {
                    val isBuffering = mediaController?.playbackState ==
                            Player.STATE_BUFFERING
                    if (!isBuffering && _playerState.value is PlayerState.Playing) {
                        _playerState.value = PlayerState.Idle
                        progressJob?.cancel()
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d(TAG, "ExoPlayer state → ${
                    when (playbackState) {
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY     -> "READY"
                        Player.STATE_ENDED     -> "ENDED"
                        else                                          -> "IDLE"
                    }
                }")
                if (playbackState == Player.STATE_ENDED) {
                    _playerState.value = PlayerState.Idle
                    progressJob?.cancel()
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                Log.e(TAG, "ExoPlayer error: ${error.errorCodeName} — ${error.message}")
                _playerState.value = PlayerState.Error(error.message ?: "Playback error")
                progressJob?.cancel()
            }
        })
    }

    fun playTrack(track: Track, newQueue: List<Track>? = null, preserveHistory: Boolean = false) {
        Log.d(TAG, "playTrack: ${track.id} '${track.title}'")
        if (_unsavedTrack.value?.id == track.id && mediaController?.isPlaying == true) {
            Log.d(TAG, "playTrack: skipped (already playing)")
            return
        }
        if (!preserveHistory) _history.value = emptyList()
        queueFetchJob?.cancel()
        playbackJob?.cancel()
        progressJob?.cancel()
        _unsavedTrack.value = track
        _currentSavedTrack.value = null
        _isSaved.value = false
        currentStreamUrl = null
        _syncedLyrics.value = emptyList()
        _isLoadingLyrics.value = true
        _positionMs.value = 0L
        _durationMs.value = 0L
        _queue.value = newQueue ?: emptyList()
        loadFavoriteStatus(track.id)
        viewModelScope.launch {
            sessionPreferences.save(
                trackId = track.id,
                title = track.title,
                artist = track.artist,
                thumbnail = track.albumImageUrl,
                isSaved = false,
            )
        }
        viewModelScope.launch {
            val cached = lyricsCache.remove(track.id)
            if (cached != null) {
                _syncedLyrics.value = cached
                _isLoadingLyrics.value = false
            } else {
                _syncedLyrics.value = loadLyrics(track.title, track.artist)
                _isLoadingLyrics.value = false
            }
        }
        if (newQueue == null) {
            queueFetchJob = viewModelScope.launch { _queue.value = queueService.getUpNextTracks(track.id) }
        }
        playbackJob = viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            val localPath = repository.getTrackByVideoId(track.id)?.audioFilePath
            val uri: String = if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
                Log.d(TAG, "playTrack: ${track.id} → local file $localPath")
                Uri.fromFile(File(localPath)).toString()
            } else {
                val streamUrl = streamService.getAudioStreamUrl(track.id)
                Log.d(TAG, "playTrack: ${track.id} → stream $streamUrl")
                currentStreamUrl = streamUrl
                streamUrl
            }
            val mediaItem = buildMediaItem(uri, track.title, track.artist, track.albumImageUrl)
            withContext(Dispatchers.Main) {
                val controller = mediaController ?: run {
                    _playerState.value = PlayerState.Error("Player not ready")
                    return@withContext
                }
                controller.setMediaItem(mediaItem)
                controller.prepare()
                controller.play()
                // PlayerState.Playing se asigna en onIsPlayingChanged cuando ExoPlayer realmente reproduce
            }
            prefetchAdjacentTracks()
        }
    }

    fun play(savedTrackId: Int) {
        if (_currentSavedTrack.value?.id == savedTrackId && mediaController?.isPlaying == true) return
        _history.value = emptyList()
        playbackJob?.cancel()
        progressJob?.cancel()
        mediaController?.stop()
        _unsavedTrack.value = null
        _isSaved.value = true
        currentStreamUrl = null
        _syncedLyrics.value = emptyList()
        _isLoadingLyrics.value = true
        _positionMs.value = 0L
        _durationMs.value = 0L
        _queue.value = emptyList()
        playbackJob = viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            val saved = repository.getTrack(savedTrackId) ?: run {
                _playerState.value = PlayerState.Error("Not found")
                return@launch
            }
            _currentSavedTrack.value = saved
            loadFavoriteStatus(saved.trackId)
            viewModelScope.launch {
                sessionPreferences.save(
                    trackId = saved.trackId,
                    title = saved.trackTitle,
                    artist = saved.trackArtist,
                    thumbnail = saved.trackThumbnailUrl,
                    isSaved = true,
                    savedDbId = saved.id,
                )
            }
            viewModelScope.launch {
                _syncedLyrics.value = loadLyrics(saved.trackTitle, saved.trackArtist)
                _isLoadingLyrics.value = false
            }
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
                val streamUrl = streamService.getAudioStreamUrl(saved.trackId)
                currentStreamUrl = streamUrl
                streamUrl.toUri()
            }

            val mediaItem = buildMediaItem(uri.toString(), saved.trackTitle, saved.trackArtist, saved.trackThumbnailUrl)
            withContext(Dispatchers.Main) {
                val controller = mediaController ?: run {
                    _playerState.value = PlayerState.Error("Player not ready")
                    return@withContext
                }
                controller.setMediaItem(mediaItem)
                controller.prepare()
                controller.play()
                // PlayerState.Playing se asigna en onIsPlayingChanged cuando ExoPlayer realmente reproduce
            }
        }
    }

    fun reorderQueue(from: Int, to: Int) {
        _queue.value = _queue.value.toMutableList().apply { add(to, removeAt(from)) }
    }

    fun skipToQueueTrack(track: Track) {
        val queue = _queue.value
        val index = queue.indexOf(track)
        if (index < 0) return
        val skipped = queue.take(index)
        buildCurrentTrack()?.let { current ->
            _history.value = _history.value + current + skipped
        } ?: run {
            _history.value = _history.value + skipped
        }
        playTrack(track, newQueue = queue.drop(index + 1), preserveHistory = true)
    }

    fun toggleFavorite() {
        val track = buildCurrentTrack() ?: return
        _isFavorite.value = !_isFavorite.value
        viewModelScope.launch {
            repository.toggleFavorite(track)
            _isFavorite.value = repository.isFavoriteTrack(track.id)
        }
    }

    private fun loadFavoriteStatus(trackId: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.isFavoriteTrack(trackId)
        }
    }

    fun downloadCurrentTrack() {
        if (_isDownloading.value) return
        val track = if (!_isSaved.value) {
            _unsavedTrack.value ?: return
        } else {
            val saved = _currentSavedTrack.value?.takeIf { it.audioFilePath.isNullOrEmpty() } ?: return
            Track(
                id = saved.trackId, title = saved.trackTitle, artist = saved.trackArtist,
                albumImageUrl = saved.trackThumbnailUrl, previewUrl = null, durationText = saved.trackDurationText
            )
        }
        viewModelScope.launch {
            _isDownloading.value = true
            SnackbarController.emit(SnackbarEvent.DownloadStarted)
            val streamUrl = currentStreamUrl ?: streamService.getAudioStreamUrl(track.id)
            val audioFilePath = streamService.downloadAudio(track.id, streamUrl, context.filesDir)
            repository.saveTrack(track, audioFilePath)
            val saved = repository.getAllTracks().first().find { it.trackId == track.id }
            _currentSavedTrack.value = saved
            _unsavedTrack.value = null
            _isSaved.value = true
            _isDownloading.value = false
            SnackbarController.emit(SnackbarEvent.DownloadSuccess)
        }
    }

    fun togglePlayPause() {
        val controller = mediaController ?: return
        when {
            controller.isPlaying -> controller.pause()
            controller.playbackState == Player.STATE_READY ||
            controller.playbackState == Player.STATE_BUFFERING -> controller.play()
            else -> loadAndPlay()
        }
    }

    private fun loadAndPlay() {
        if (_isSaved.value) {
            val saved = _currentSavedTrack.value ?: return
            play(saved.id)
        } else {
            val track = _unsavedTrack.value ?: return
            playTrack(track)
        }
    }

    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        _positionMs.value = positionMs
    }

    fun skipToNext() {
        val queue = _queue.value
        Log.d(TAG, "skipToNext: queue size=${queue.size}")
        if (queue.isEmpty()) {
            Log.d(TAG, "skipToNext: queue exhausted → Idle")
            mediaController?.stop()
            progressJob?.cancel()
            _playerState.value = PlayerState.Idle
            return
        }
        buildCurrentTrack()?.let { _history.value = _history.value + it }
        val next = queue.first()
        Log.d(TAG, "skipToNext: playing next → ${next.id} '${next.title}'")
        playTrack(next, newQueue = queue.drop(1), preserveHistory = true)
    }

    fun skipToPrevious(force: Boolean = false) {
        if (!force && _positionMs.value > 3_000L) {
            seekTo(0)
            return
        }
        val history = _history.value
        if (history.isEmpty()) {
            seekTo(0)
            return
        }
        val previous = history.last()
        val newQueue = buildCurrentTrack()?.let { listOf(it) + _queue.value } ?: _queue.value
        _history.value = history.dropLast(1)
        playTrack(previous, newQueue = newQueue, preserveHistory = true)
    }

    private fun buildCurrentTrack(): Track? = if (_isSaved.value) {
        _currentSavedTrack.value?.let {
            Track(
                id = it.trackId, title = it.trackTitle, artist = it.trackArtist,
                albumImageUrl = it.trackThumbnailUrl, previewUrl = null, durationText = it.trackDurationText
            )
        }
    } else {
        _unsavedTrack.value
    }

    private fun buildMediaItem(uri: String, title: String, artist: String, artworkUrl: String?): MediaItem =
        MediaItem.Builder()
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setArtworkUri(artworkUrl?.toUri())
                    .build()
            )
            .build()

    private fun restoreLastTrack() {
        viewModelScope.launch {
            val last = sessionPreferences.lastTrack.firstOrNull() ?: return@launch
            if (_currentSavedTrack.value != null || _unsavedTrack.value != null) return@launch
            if (last.isSaved && last.savedDbId != -1) {
                val saved = repository.getTrack(last.savedDbId) ?: return@launch
                _currentSavedTrack.value = saved
                _isSaved.value = true
                loadFavoriteStatus(saved.trackId)
            } else {
                _unsavedTrack.value = Track(
                    id = last.trackId,
                    title = last.title,
                    artist = last.artist,
                    albumImageUrl = last.thumbnail,
                    previewUrl = null,
                    durationText = ""
                )
                _isSaved.value = false
                loadFavoriteStatus(last.trackId)
            }
        }
    }

    private suspend fun loadLyrics(title: String, artist: String): List<LyricLine> {
        val fromBackend = backendService.getLyrics(title, artist)
        if (fromBackend.isNotEmpty()) return fromBackend
        return lrcLibService.getSyncedLyrics(title, artist)
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                _positionMs.value = mediaController?.currentPosition ?: 0L
                _durationMs.value = mediaController?.duration?.takeIf { it > 0 } ?: 0L
                delay(500)
            }
        }
    }

    private fun prefetchAdjacentTracks() {
        prefetchJob?.cancel()
        val adjacent = buildList {
            _queue.value.firstOrNull()?.let { add(it) }
            _history.value.lastOrNull()?.let { add(it) }
        }
        if (adjacent.isEmpty()) return
        prefetchJob = viewModelScope.launch {
            for (track in adjacent) {
                launch {
                    Log.d(TAG, "prefetch: warming backend stream for ${track.id}")
                    backendService.warmStream(track.id)
                }
                if (!lyricsCache.containsKey(track.id)) {
                    launch {
                        Log.d(TAG, "prefetch: lyrics for ${track.id}")
                        lyricsCache[track.id] = loadLyrics(track.title, track.artist)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        playbackJob?.cancel()
        progressJob?.cancel()
        prefetchJob?.cancel()
        queueFetchJob?.cancel()
        MediaController.releaseFuture(controllerFuture)
        super.onCleared()
    }

    companion object {
        private const val TAG = "PlaybackViewModel"
    }
}
