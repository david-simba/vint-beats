package com.davidsimba.vintbeats.feature.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.feature.library.domain.artist.SavedArtistRepository
import com.davidsimba.vintbeats.feature.onboarding.OnboardingPreferences
import com.davidsimba.vintbeats.feature.search.domain.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val artistRepository: SavedArtistRepository,
    private val onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Artist>>(emptyList())
    val results: StateFlow<List<Artist>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _step = MutableStateFlow(0)
    val step: StateFlow<Int> = _step.asStateFlow()

    private val _selected = MutableStateFlow<List<Artist>>(emptyList())
    val selected: StateFlow<List<Artist>> = _selected.asStateFlow()

    init {
        _query
            .debounce(400)
            .distinctUntilChanged()
            .filter { it.length >= 2 }
            .onEach { search(it) }
            .launchIn(viewModelScope)
    }

    fun onNameChange(name: String) { _name.value = name }

    fun goToArtists() { if (_name.value.isNotBlank()) _step.value = 1 }
    fun goBack() { _step.value = 0 }

    fun onQueryChange(query: String) {
        _query.value = query
        if (query.isEmpty()) _results.value = emptyList()
    }

    fun toggleArtist(artist: Artist) {
        _selected.value = _selected.value.toMutableList().apply {
            if (any { it.id == artist.id }) removeAll { it.id == artist.id }
            else add(artist)
        }
    }

    fun isSelected(artistId: String) = _selected.value.any { it.id == artistId }

    fun complete(onDone: () -> Unit) {
        val artists = _selected.value
        if (artists.size < 3 || _name.value.isBlank()) return
        viewModelScope.launch {
            onboardingPreferences.setName(_name.value.trim())
            artists.forEach { artistRepository.saveArtist(it.id, it.name, it.thumbnailUrl) }
            onboardingPreferences.setComplete(true)
            onDone()
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching { searchRepository.searchArtists(query) }
                .onSuccess { _results.value = it }
                .onFailure { _results.value = emptyList() }
            _isLoading.value = false
        }
    }
}
