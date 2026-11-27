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
import com.davidsimba.vintbeats.core.common.QueueController
import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.player.contract.LyricsProvider
import com.davidsimba.vintbeats.feature.player.contract.PlaybackObserver
import com.davidsimba.vintbeats.feature.player.contract.PlayerLibrary
import com.davidsimba.vintbeats.feature.player.contract.PlayerSettings
import com.davidsimba.vintbeats.feature.player.contract.StreamResolver
import com.davidsimba.vintbeats.feature.player.contract.TrackLibraryStatus
import com.davidsimba.vintbeats.feature.player.contract.UpNextProvider
import com.davidsimba.vintbeats.feature.player.widget.NowPlayingWidgetUpdater
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Reproduce una cola de [Track]. No sabe de biblioteca, descargas ni de dónde sale el audio:
 * todo eso entra por las interfaces de `feature.player.contract`.
 */
@HiltViewModel
class PlaybackViewModel @OptIn(UnstableApi::class)
@Inject constructor(
    private val streamResolver: StreamResolver,
    private val lyricsProvider: LyricsProvider,
    private val upNextProvider: UpNextProvider,
    private val library: PlayerLibrary,
    private val settings: PlayerSettings,
    private val observer: PlaybackObserver,
    private val sessionPreferences: PlayerSessionPreferences,
    private val widgetUpdater: NowPlayingWidgetUpdater,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>
    private var currentStreamUrl: String? = null

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    val currentlyPlayingTrackId: StateFlow<String?> = _currentTrack
        .map { it?.id }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Estado de la canción actual en la biblioteca (favorito, descarga, archivo local). */
    @OptIn(ExperimentalCoroutinesApi::class)
    val libraryStatus: StateFlow<TrackLibraryStatus> = currentlyPlayingTrackId
        .flatMapLatest { id -> if (id == null) flowOf(TrackLibraryStatus()) else library.status(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrackLibraryStatus())

    val isFavorite: StateFlow<Boolean> = libraryStatus
        .map { it.isFavorite }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isDownloading: StateFlow<Boolean> = libraryStatus
        .map { it.isDownloading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hasLocalFile: StateFlow<Boolean> = libraryStatus
        .map { it.hasLocalFile }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

    private val _isQueueLoading = MutableStateFlow(false)
    val isQueueLoading: StateFlow<Boolean> = _isQueueLoading.asStateFlow()

    private val _history = MutableStateFlow<List<Track>>(emptyList())
    val history: StateFlow<List<Track>> = _history.asStateFlow()

    val equalizerEnabled: StateFlow<Boolean> = settings.showEqualizer
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val coverAsBackground: StateFlow<Boolean> = settings.coverAsBackground
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _repeatMode = MutableStateFlow(Player.REPEAT_MODE_OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _shuffleEnabled = MutableStateFlow(false)
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled.asStateFlow()

    private val _originalQueue = MutableStateFlow<List<Track>>(emptyList())

    private val _playingFrom = MutableStateFlow<PlayingFrom?>(null)
    val playingFrom: StateFlow<PlayingFrom?> = _playingFrom.asStateFlow()

    fun setPlayingFrom(source: PlayingFrom?) { _playingFrom.value = source }

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

        viewModelScope.launch {
            QueueController.events.collect { track ->
                _queue.value = listOf(track) + _queue.value
                if (_originalQueue.value.isNotEmpty()) {
                    _originalQueue.value = listOf(track) + _originalQueue.value
                }
            }
        }

        restoreLastTrack()
    }

    private fun setupControllerListener(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onRepeatModeChanged(repeatMode: Int) {
                _repeatMode.value = repeatMode
            }

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
                viewModelScope.launch {
                    val track = _currentTrack.value ?: return@launch
                    widgetUpdater.update(track.title, track.artist, track.albumImageUrl, isPlaying)
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
        if (_currentTrack.value?.id == track.id && mediaController?.isPlaying == true) {
            Log.d(TAG, "playTrack: skipped (already playing)")
            // Misma canción sonando: solo se adopta la cola del nuevo contexto.
            if (newQueue != null) {
                _queue.value = newQueue.filter { it.id != track.id }
                _originalQueue.value = emptyList()
            }
            return
        }
        if (!preserveHistory) _history.value = emptyList()
        queueFetchJob?.cancel()
        playbackJob?.cancel()
        progressJob?.cancel()
        _currentTrack.value = track
        currentStreamUrl = null
        _syncedLyrics.value = emptyList()
        _isLoadingLyrics.value = true
        _positionMs.value = 0L
        _durationMs.value = 0L
        when {
            newQueue == null -> {
                _queue.value = emptyList()
                _originalQueue.value = emptyList()
            }
            _shuffleEnabled.value -> {
                _originalQueue.value = newQueue
                _queue.value = newQueue.shuffled()
            }
            else -> {
                _queue.value = newQueue
                _originalQueue.value = emptyList()
            }
        }
        viewModelScope.launch { observer.onTrackStarted(track) }
        viewModelScope.launch { widgetUpdater.update(track.title, track.artist, track.albumImageUrl, false) }
        viewModelScope.launch {
            sessionPreferences.save(
                trackId = track.id,
                title = track.title,
                artist = track.artist,
                thumbnail = track.albumImageUrl,
            )
        }
        viewModelScope.launch {
            val cached = lyricsCache.remove(track.id)
            if (cached != null) {
                _syncedLyrics.value = cached
                _isLoadingLyrics.value = false
            } else {
                _syncedLyrics.value = lyricsProvider.syncedLyrics(track.title, track.artist)
                _isLoadingLyrics.value = false
            }
        }
        if (newQueue == null) {
            _isQueueLoading.value = true
            queueFetchJob = viewModelScope.launch {
                _queue.value = upNextProvider.upNext(track.id)
                _isQueueLoading.value = false
            }
        }
        playbackJob = viewModelScope.launch {
            _playerState.value = PlayerState.Loading
            val localPath = withContext(Dispatchers.IO) { library.status(track.id).first().localAudioPath }
            val uri: String = if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
                Log.d(TAG, "playTrack: ${track.id} → local file $localPath")
                Uri.fromFile(File(localPath)).toString()
            } else {
                val streamUrl = streamResolver.resolve(track.id)
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

    fun reorderQueue(from: Int, to: Int) {
        _queue.value = _queue.value.toMutableList().apply { add(to, removeAt(from)) }
    }

    fun removeFromQueue(track: Track) {
        _queue.value = _queue.value.filter { it.id != track.id }
        if (_originalQueue.value.isNotEmpty()) {
            _originalQueue.value = _originalQueue.value.filter { it.id != track.id }
        }
    }

    fun skipToQueueTrack(track: Track) {
        val queue = _queue.value
        val index = queue.indexOf(track)
        if (index < 0) return
        val skipped = queue.take(index)
        _currentTrack.value?.let { current ->
            _history.value = _history.value + current + skipped
        } ?: run {
            _history.value = _history.value + skipped
        }
        playTrack(track, newQueue = queue.drop(index + 1), preserveHistory = true)
    }

    fun toggleCoverStyle() {
        viewModelScope.launch { settings.setCoverAsBackground(!coverAsBackground.value) }
    }

    fun toggleEqualizer() {
        viewModelScope.launch { settings.setShowEqualizer(!equalizerEnabled.value) }
    }

    fun toggleFavorite() {
        val track = _currentTrack.value ?: return
        viewModelScope.launch { library.toggleFavorite(track) }
    }

    fun downloadCurrentTrack() {
        val track = _currentTrack.value ?: return
        if (libraryStatus.value.isDownloading || libraryStatus.value.hasLocalFile) return
        viewModelScope.launch { library.download(track, currentStreamUrl) }
    }

    fun toggleShuffle() {
        val newValue = !_shuffleEnabled.value
        _shuffleEnabled.value = newValue
        if (newValue) {
            _originalQueue.value = _queue.value
            _queue.value = _queue.value.shuffled()
        } else {
            val remaining = _queue.value.map { it.id }.toSet()
            _queue.value = _originalQueue.value.filter { it.id in remaining }
            _originalQueue.value = emptyList()
        }
    }

    fun toggleRepeatMode() {
        val next = if (_repeatMode.value == Player.REPEAT_MODE_OFF)
            Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        _repeatMode.value = next
        mediaController?.repeatMode = next
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
        val track = _currentTrack.value ?: return
        playTrack(track, newQueue = _queue.value.takeIf { it.isNotEmpty() })
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
        _currentTrack.value?.let { _history.value = _history.value + it }
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
        val newQueue = _currentTrack.value?.let { listOf(it) + _queue.value } ?: _queue.value
        _history.value = history.dropLast(1)
        playTrack(previous, newQueue = newQueue, preserveHistory = true)
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
            if (_currentTrack.value != null) return@launch
            val restored = Track(
                id = last.trackId,
                title = last.title,
                artist = last.artist,
                albumImageUrl = last.thumbnail,
                previewUrl = null,
                durationText = ""
            )
            _currentTrack.value = restored
            loadRestoredExtras(restored)
        }
    }

    /** La canción restaurada aún no suena, pero su letra y "A continuación" deben verse ya. */
    private fun loadRestoredExtras(track: Track) {
        _isLoadingLyrics.value = true
        viewModelScope.launch {
            val lines = lyricsProvider.syncedLyrics(track.title, track.artist)
            if (_currentTrack.value?.id == track.id) _syncedLyrics.value = lines
            _isLoadingLyrics.value = false
        }
        _isQueueLoading.value = true
        queueFetchJob = viewModelScope.launch {
            val upNext = upNextProvider.upNext(track.id)
            if (_currentTrack.value?.id == track.id && _queue.value.isEmpty()) _queue.value = upNext
            _isQueueLoading.value = false
        }
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
                    Log.d(TAG, "prefetch: warming stream for ${track.id}")
                    streamResolver.prefetch(track.id)
                }
                if (!lyricsCache.containsKey(track.id)) {
                    launch {
                        Log.d(TAG, "prefetch: lyrics for ${track.id}")
                        lyricsCache[track.id] = lyricsProvider.syncedLyrics(track.title, track.artist)
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

data class PlayingFrom(val name: String, val route: String? = null)
