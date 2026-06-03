package com.davidsimba.vintbeats.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.HomeSectionPlaylists
import com.davidsimba.vintbeats.core.model.PlaylistItem
import com.davidsimba.vintbeats.core.youtube.ArtistInput
import com.davidsimba.vintbeats.core.youtube.BackendService
import com.davidsimba.vintbeats.feature.library.domain.artist.SavedArtistRepository
import com.davidsimba.vintbeats.feature.onboarding.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Success(val sections: List<HomeSectionPlaylists>) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val artistRepository: SavedArtistRepository,
    private val backendService: BackendService,
    onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    // "Para ti" acumula una playlist por artista — siempre va primero
    private val _paraPlaylists = MutableStateFlow<List<PlaylistItem>>(emptyList())
    // Secciones extra: "Fans también escuchan" + "Descubre" — van después
    private val _extraSections = MutableStateFlow<List<HomeSectionPlaylists>>(emptyList())
    private val _initialLoad = MutableStateFlow(true)

    val uiState: StateFlow<HomeUiState> = combine(
        _paraPlaylists, _extraSections, _initialLoad
    ) { para, extra, loading ->
        val sections = buildList {
            if (para.isNotEmpty()) add(HomeSectionPlaylists("Para ti", para))
            addAll(extra)
        }
        when {
            loading -> HomeUiState.Loading
            sections.isEmpty() -> HomeUiState.Empty
            else -> HomeUiState.Success(sections)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)

    val needsOnboarding: StateFlow<Boolean?> = onboardingPreferences.isComplete
        .map { !it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userName: StateFlow<String> = onboardingPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun loadFeed() {
        if (_paraPlaylists.value.isNotEmpty() || _extraSections.value.isNotEmpty()) return
        viewModelScope.launch {
            _paraPlaylists.value = emptyList()
            _extraSections.value = emptyList()
            _initialLoad.value = true

            val artists = artistRepository.getSavedArtists().first()
            if (artists.isEmpty()) {
                _initialLoad.value = false
                return@launch
            }

            val jobs = buildList {
                // Una request por artista — cada playlist llega y se añade a "Para ti" inmediatamente
                artists.forEach { artist ->
                    add(launch {
                        backendService
                            .getHomeFeedPlaylists(listOf(ArtistInput(artist.artistId, artist.name)))
                            .filter { it.title.startsWith("Porque") }
                            .flatMap { it.playlists }
                            .forEach { playlist ->
                                _paraPlaylists.update { it + playlist }
                                _initialLoad.value = false
                            }
                    })
                }
                // Request combinada solo para "Fans también escuchan" + "Descubre"
                add(launch {
                    val extra = backendService
                        .getHomeFeedPlaylists(artists.map { ArtistInput(it.artistId, it.name) })
                        .filter { !it.title.startsWith("Porque") }
                    if (extra.isNotEmpty()) {
                        _extraSections.value = extra
                        _initialLoad.value = false
                    }
                })
            }

            jobs.joinAll()
            _initialLoad.value = false
        }
    }
}
