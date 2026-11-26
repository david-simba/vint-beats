package com.davidsimba.vintbeats.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.network.ArtistInput
import com.davidsimba.vintbeats.core.network.BackendService
import com.davidsimba.vintbeats.core.common.toHighRes
import com.davidsimba.vintbeats.feature.home.data.HomeFeedCache
import com.davidsimba.vintbeats.core.data.recent.RecentlyPlayedAlbumsRepository
import com.davidsimba.vintbeats.core.data.recent.RecentCollectionsRepository
import com.davidsimba.vintbeats.core.data.recent.RecentlyPlayedRepository
import com.davidsimba.vintbeats.feature.home.domain.ArtistRadioItem
import com.davidsimba.vintbeats.core.model.RecentAlbum
import com.davidsimba.vintbeats.core.model.RecentCollection
import com.davidsimba.vintbeats.core.model.RecentCollectionKind
import com.davidsimba.vintbeats.core.model.RecentTrack
import com.davidsimba.vintbeats.feature.home.domain.QuickAccessItem
import com.davidsimba.vintbeats.core.model.HomeSectionPlaylists
import com.davidsimba.vintbeats.core.model.PlaylistItem
import com.davidsimba.vintbeats.core.data.library.SavedArtistRepository
import com.davidsimba.vintbeats.core.data.preferences.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
        val artistRadios: List<ArtistRadioItem>,
    ) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val artistRepository: SavedArtistRepository,
    private val backendService: BackendService,
    private val homeFeedCache: HomeFeedCache,
    private val recentCollectionsRepository: RecentCollectionsRepository,
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val recentlyPlayedAlbumsRepository: RecentlyPlayedAlbumsRepository,
    onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    private val _quickMix = MutableStateFlow<List<Track>>(emptyList())
    private val _paraPlaylists = MutableStateFlow<List<PlaylistItem>>(emptyList())
    private val _extraSections = MutableStateFlow<List<HomeSectionPlaylists>>(emptyList())
    private val _initialLoad = MutableStateFlow(true)
    private val _artistRadios = MutableStateFlow<List<ArtistRadioItem>>(emptyList())

    val uiState: StateFlow<HomeUiState> = combine(
        _quickMix, _paraPlaylists, _extraSections, _initialLoad, _artistRadios
    ) { mix, para, extra, loading, radios ->
        val sections = buildList {
            if (para.isNotEmpty()) add(HomeSectionPlaylists("Artistas que escuchas", para, isPrimary = true))
            addAll(extra)
        }
        when {
            loading -> HomeUiState.Loading
            mix.isEmpty() && sections.isEmpty() -> HomeUiState.Empty
            else -> HomeUiState.Success(mix, sections, radios)
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

    val recentAlbums: StateFlow<List<RecentAlbum>> = recentlyPlayedAlbumsRepository.getRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val recentTracks: StateFlow<List<RecentTrack>> = recentlyPlayedRepository.getRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val recentCollections: StateFlow<List<RecentCollection>> = recentCollectionsRepository.getRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** "Canciones que te gustan" primero; luego álbumes, canciones y playlists recientes, intercalados y sin duplicados. */
    val quickAccess: StateFlow<List<QuickAccessItem>> = combine(recentAlbums, recentTracks, recentCollections) { albums, tracks, collections ->
        val albumItems = albums.map { QuickAccessItem.Album(it.albumId, it.title, it.thumbnailUrl) }
        val playlistItems = collections.filter { it.kind == RecentCollectionKind.PLAYLIST }.map {
            QuickAccessItem.Playlist(it.collectionId, it.title, it.thumbnailUrl, it.artistId, it.artistName)
        }
        val trackItems = tracks.map { QuickAccessItem.Track(it.trackId, it.title, it.artist, it.thumbnailUrl) }
        val lists = listOf<List<QuickAccessItem>>(albumItems, trackItems, playlistItems)
        val interleaved = (0 until lists.maxOf { it.size }).flatMap { i -> lists.mapNotNull { it.getOrNull(i) } }
        (listOf(QuickAccessItem.Favorites) + interleaved).distinctBy { it.key }.take(6)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(QuickAccessItem.Favorites))

    fun recordPlaylist(id: String, thumbnailUrl: String?, artistId: String?, artistName: String?) {
        val known = _paraPlaylists.value.firstOrNull { it.id == id }
            ?: _extraSections.value.flatMap { it.playlists }.firstOrNull { it.id == id }
        val title = artistName ?: known?.title ?: return
        viewModelScope.launch {
            recentCollectionsRepository.save(RecentCollectionKind.PLAYLIST, id, title, thumbnailUrl, artistId, artistName)
        }
    }

    fun loadFeed() {
        if (_paraPlaylists.value.isNotEmpty() || _quickMix.value.isNotEmpty()) return
        viewModelScope.launch {
            _initialLoad.value = true

            val artists = artistRepository.getSavedArtists().first()
            if (artists.isEmpty()) {
                _initialLoad.value = false
                return@launch
            }

            // Para ti: instant from local DB, no network needed
            _paraPlaylists.value = artists.map { artist ->
                PlaylistItem(
                    id = artist.artistId,
                    title = artist.name,
                    subtitle = "",
                    thumbnailUrl = artist.thumbnailUrl.toHighRes(),
                    artistId = artist.artistId,
                    artistName = artist.name,
                )
            }
            _initialLoad.value = false

            // Restore from cache if data is from today
            val cached = homeFeedCache.get()
            if (cached != null) {
                if (cached.quickMix.isNotEmpty()) _quickMix.value = cached.quickMix
                if (cached.extraSections.isNotEmpty()) _extraSections.value = cached.extraSections
                if (cached.artistRadios.isNotEmpty()) _artistRadios.value = cached.artistRadios
                return@launch
            }

            // Cache miss: fetch all three in parallel, update UI as each completes
            val artistInputs = artists.map { ArtistInput(it.artistId, it.name, it.thumbnailUrl) }

            val mixDeferred = async {
                val mix = backendService.getQuickMix(artistInputs)
                if (mix.isNotEmpty()) _quickMix.value = mix
                mix
            }

            val extraDeferred = async {
                val extra = backendService
                    .getHomeFeedPlaylists(artistInputs)
                    .filter { !it.title.startsWith("Porque") }
                if (extra.isNotEmpty()) _extraSections.value = extra
                extra
            }

            val radiosDeferred = async {
                val radios = mutableListOf<ArtistRadioItem>()
                artists.forEach { artist ->
                    val result = backendService.getArtistRadio(artist.artistId)
                    if (result != null) {
                        val (images, tracks) = result
                        radios.add(ArtistRadioItem(artist.artistId, artist.name, images, tracks))
                    }
                }
                if (radios.isNotEmpty()) _artistRadios.value = radios
                radios.toList()
            }

            val mix = mixDeferred.await()
            val extra = extraDeferred.await()
            val radios = radiosDeferred.await()

            // Only cache if we got meaningful data (guards against offline first-run)
            if (mix.isNotEmpty() || extra.isNotEmpty()) {
                homeFeedCache.save(mix, extra, radios)
            }
        }
    }
}
