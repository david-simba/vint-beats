package com.davidsimba.vintbeats.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.youtube.ArtistInput
import com.davidsimba.vintbeats.core.youtube.BackendService
import com.davidsimba.vintbeats.core.util.toHighRes
import com.davidsimba.vintbeats.feature.home.domain.HomeSectionPlaylists
import com.davidsimba.vintbeats.feature.home.domain.PlaylistItem
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
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Success(
        val quickMix: List<Track>,
        val sections: List<HomeSectionPlaylists>,
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val artistRepository: SavedArtistRepository,
    private val backendService: BackendService,
    onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    private val _quickMix = MutableStateFlow<List<Track>>(emptyList())
    private val _paraPlaylists = MutableStateFlow<List<PlaylistItem>>(emptyList())
    private val _extraSections = MutableStateFlow<List<HomeSectionPlaylists>>(emptyList())
    private val _initialLoad = MutableStateFlow(true)

    val uiState: StateFlow<HomeUiState> = combine(
        _quickMix, _paraPlaylists, _extraSections, _initialLoad
    ) { mix, para, extra, loading ->
        val sections = buildList {
            if (para.isNotEmpty()) add(HomeSectionPlaylists("Artistas que escuchas", para, isPrimary = true))
            addAll(extra)
        }
        when {
            loading -> HomeUiState.Loading
            mix.isEmpty() && sections.isEmpty() -> HomeUiState.Empty
            else -> HomeUiState.Success(mix, sections)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)

    val needsOnboarding: StateFlow<Boolean?> = onboardingPreferences.isComplete
        .map { !it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private var onboardingNavigationConsumed = false

    fun tryConsumeOnboardingNavigation(): Boolean {
        if (onboardingNavigationConsumed) return false
        onboardingNavigationConsumed = true
        return true
    }

    val userName: StateFlow<String> = onboardingPreferences.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun loadFeed() {
        if (_paraPlaylists.value.isNotEmpty() || _quickMix.value.isNotEmpty()) return
        viewModelScope.launch {
            _quickMix.value = emptyList()
            _paraPlaylists.value = emptyList()
            _extraSections.value = emptyList()
            _initialLoad.value = true

            val artists = artistRepository.getSavedArtists().first()
            if (artists.isEmpty()) {
                _initialLoad.value = false
                return@launch
            }

            val artistInputs = artists.map { ArtistInput(it.artistId, it.name, it.thumbnailUrl) }

            // Quick mix — cacheado en memoria, no en servidor
            launch {
                val mix = backendService.getQuickMix(artistInputs)
                if (mix.isNotEmpty()) _quickMix.value = mix
            }

            // Para ti: cards instantáneas desde los artistas guardados
            _paraPlaylists.value = artists.map { artist ->
                PlaylistItem(
                    id = artist.artistId,
                    title = "This Is ${artist.name}",
                    subtitle = "",
                    thumbnailUrl = artist.thumbnailUrl.toHighRes(),
                    artistId = artist.artistId,
                    artistName = artist.name,
                )
            }
            _initialLoad.value = false

            // Extra: Fans también escuchan + Descubre
            val extra = backendService
                .getHomeFeedPlaylists(artistInputs)
                .filter { !it.title.startsWith("Porque") }
            if (extra.isNotEmpty()) _extraSections.value = extra
        }
    }
}
