package com.davidsimba.vintbeats.dj

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.data.library.SavedArtistRepository
import com.davidsimba.vintbeats.core.data.preferences.OnboardingPreferences
import com.davidsimba.vintbeats.core.model.DjSession
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.network.BackendService
import com.davidsimba.vintbeats.core.network.DjRecent
import com.davidsimba.vintbeats.core.network.DjResult
import com.davidsimba.vintbeats.feature.player.ui.PlaybackViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

enum class DjPhase { Idle, Loading, Live }

enum class DjError { RateLimited, NotFound, Generic }

data class DjUiState(
    val phase: DjPhase = DjPhase.Idle,
    val error: DjError? = null,
    /** Lo último que dijo el DJ. */
    val caption: String = "",
    val speaking: Boolean = false,
    /** El DJ está preparando un cambio de rumbo. */
    val changing: Boolean = false,
)

private const val EXTEND_WHEN_QUEUE_AT_MOST = 2
private const val RECENT_LIMIT = 10
private const val FAVORITES_LIMIT = 10
private const val CHANGE_COURSE_PROMPT = "Change the mood: something clearly different from the recent songs"

@HiltViewModel
class DjViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val backend: BackendService,
    private val savedArtists: SavedArtistRepository,
    onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    private val defaultPrompt = context.getString(R.string.dj_default_prompt)

    val userName: StateFlow<String> = onboardingPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val speaker = DjSpeaker(context)
    private val _state = MutableStateFlow(DjUiState())
    val state: StateFlow<DjUiState> = _state.asStateFlow()

    // Canciones que puso el DJ, y los textos de enlace entre sets.
    private val djTrackIds = mutableSetOf<String>()
    private val outroAfter = mutableMapOf<String, String>()
    private val introBefore = mutableMapOf<String, String>()
    private var lastPlayedId: String? = null
    private var prompt = ""
    private var playback: PlaybackViewModel? = null
    private var sessionJob: Job? = null
    private var monitorJob: Job? = null
    private var fetchingMore = false

    private val language: String
        get() = if (Locale.getDefault().language == "es") "Spanish" else "English"

    /** Empieza una sesión. Sin [userPrompt] usa el último pedido (reintento) o una mezcla general. */
    fun start(playbackViewModel: PlaybackViewModel, userPrompt: String? = null) {
        if (_state.value.phase == DjPhase.Loading) return
        val text = userPrompt?.trim()?.takeIf { it.isNotEmpty() } ?: prompt.ifBlank { defaultPrompt }
        stop()
        prompt = text
        playback = playbackViewModel
        _state.value = DjUiState(phase = DjPhase.Loading)
        sessionJob = viewModelScope.launch {
            val favorites = savedArtists.getSavedArtists().first().map { it.name }.take(FAVORITES_LIMIT)
            when (val result = backend.djSession(text, language, favoriteArtists = favorites)) {
                is DjResult.Success -> goLive(result.session, playbackViewModel)
                DjResult.RateLimited -> fail(DjError.RateLimited)
                DjResult.NotFound -> fail(DjError.NotFound)
                DjResult.Failure -> fail(DjError.Generic)
            }
        }
    }

    private suspend fun goLive(session: DjSession, playbackViewModel: PlaybackViewModel) {
        register(session, isFirstSet = true)
        lastPlayedId = null
        playbackViewModel.beforePlayHook = ::beforePlay
        _state.value = DjUiState(phase = DjPhase.Live, caption = session.intro)
        if (session.intro.isNotBlank()) {
            _state.value = _state.value.copy(speaking = true)
            speaker.speak(session.intro)
            _state.value = _state.value.copy(speaking = false)
        }
        val tracks = session.items.map { it.track }
        playbackViewModel.setPlayingFrom(null)
        playbackViewModel.playTrack(tracks.first(), newQueue = tracks.drop(1))
        monitorQueue(playbackViewModel)
    }

    /**
     * Registra un set: su cierre se dice tras su última canción y, si no es el primero,
     * su apertura (un enlace corto) antes de su primera canción. La apertura del primer set se dice al empezar.
     */
    private fun register(session: DjSession, isFirstSet: Boolean = false) {
        val ids = session.items.map { it.track.id }
        djTrackIds += ids
        if (session.outro.isNotBlank()) ids.lastOrNull()?.let { outroAfter[it] = session.outro }
        if (!isFirstSet && session.intro.isNotBlank()) ids.firstOrNull()?.let { introBefore[it] = session.intro }
    }

    /** Antes de cada canción: si cambia de set, se pausa y el DJ dice el cierre del anterior y la apertura del nuevo. */
    private suspend fun beforePlay(track: Track) {
        val lines = listOfNotNull(lastPlayedId?.let { outroAfter.remove(it) }, introBefore.remove(track.id))
        lastPlayedId = track.id
        if (lines.isEmpty()) return
        playback?.pausePlayback()
        try {
            for (line in lines) {
                _state.value = _state.value.copy(caption = line, speaking = true)
                speaker.speak(line)
            }
        } finally {
            _state.value = _state.value.copy(speaking = false)
        }
    }

    /** Cuando quedan pocas canciones por sonar, pide otra tanda con lo ya reproducido. */
    private fun monitorQueue(playbackViewModel: PlaybackViewModel) {
        monitorJob?.cancel()
        monitorJob = viewModelScope.launch {
            playbackViewModel.queue.collect { queue ->
                val current = playbackViewModel.currentTrack.value
                if (current?.id !in djTrackIds || queue.size > EXTEND_WHEN_QUEUE_AT_MOST || fetchingMore) return@collect
                fetchingMore = true
                try {
                    val recent = (playbackViewModel.history.value + listOfNotNull(current))
                        .takeLast(RECENT_LIMIT)
                        .map { DjRecent(it.artist, it.title) }
                    val favorites = savedArtists.getSavedArtists().first().map { it.name }.take(FAVORITES_LIMIT)
                    val result = backend.djSession(prompt, language, recent, favorites, next = true)
                    if (result is DjResult.Success) {
                        register(result.session)
                        playbackViewModel.appendToQueue(result.session.items.map { it.track })
                    }
                } finally {
                    fetchingMore = false
                }
            }
        }
    }

    /** Cambia de rumbo: pide otra tanda con un mood distinto y la reproduce de inmediato. */
    fun changeCourse() {
        val pb = playback ?: return
        val current = _state.value
        if (current.phase != DjPhase.Live || current.changing) return
        _state.value = current.copy(changing = true)
        viewModelScope.launch {
            try {
                val recent = (pb.history.value + listOfNotNull(pb.currentTrack.value))
                    .takeLast(RECENT_LIMIT)
                    .map { DjRecent(it.artist, it.title) }
                val favorites = savedArtists.getSavedArtists().first().map { it.name }.take(FAVORITES_LIMIT)
                val result = backend.djSession(CHANGE_COURSE_PROMPT, language, recent, favorites, next = true)
                if (result is DjResult.Success) {
                    // El rumbo anterior queda atrás: se descartan sus cierres pendientes.
                    outroAfter.clear()
                    introBefore.clear()
                    register(result.session)
                    val tracks = result.session.items.map { it.track }
                    pb.playTrack(tracks.first(), newQueue = tracks.drop(1), preserveHistory = true)
                }
            } finally {
                _state.value = _state.value.copy(changing = false)
            }
        }
    }

    private fun fail(error: DjError) {
        _state.value = DjUiState(phase = DjPhase.Idle, error = error)
    }

    fun stop() {
        sessionJob?.cancel()
        monitorJob?.cancel()
        speaker.stop()
        playback?.beforePlayHook = null
        djTrackIds.clear()
        outroAfter.clear()
        introBefore.clear()
        lastPlayedId = null
        fetchingMore = false
        prompt = ""
        _state.value = DjUiState()
    }

    override fun onCleared() {
        stop()
        speaker.shutdown()
    }
}
