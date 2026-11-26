package com.davidsimba.vintbeats.player

import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.network.BackendService
import com.davidsimba.vintbeats.core.network.LrcLibService
import com.davidsimba.vintbeats.core.network.YouTubeQueueService
import com.davidsimba.vintbeats.core.data.recent.RecentlyPlayedRepository
import com.davidsimba.vintbeats.core.data.library.TrackRepository
import com.davidsimba.vintbeats.core.data.preferences.OnboardingPreferences
import com.davidsimba.vintbeats.feature.player.contract.LyricsProvider
import com.davidsimba.vintbeats.feature.player.contract.PlaybackObserver
import com.davidsimba.vintbeats.feature.player.contract.PlayerLibrary
import com.davidsimba.vintbeats.feature.player.contract.PlayerSettings
import com.davidsimba.vintbeats.feature.player.contract.StreamResolver
import com.davidsimba.vintbeats.feature.player.contract.TrackLibraryStatus
import com.davidsimba.vintbeats.feature.player.contract.UpNextProvider
import com.davidsimba.vintbeats.core.data.download.TrackDownloader
import com.davidsimba.vintbeats.core.data.stream.YouTubeStreamService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** NewPipe como fuente principal y el backend como respaldo (ver [YouTubeStreamService]). */
class AppStreamResolver @Inject constructor(
    private val streamService: YouTubeStreamService,
    private val backendService: BackendService,
) : StreamResolver {
    override suspend fun resolve(trackId: String): String = streamService.getAudioStreamUrl(trackId)
    override suspend fun prefetch(trackId: String) = backendService.warmStream(trackId)
}

/** Letras del backend y, si no hay, de LrcLib. */
class AppLyricsProvider @Inject constructor(
    private val backendService: BackendService,
    private val lrcLibService: LrcLibService,
) : LyricsProvider {
    override suspend fun syncedLyrics(title: String, artist: String): List<LyricLine> {
        val fromBackend = backendService.getLyrics(title, artist)
        if (fromBackend.isNotEmpty()) return fromBackend
        return lrcLibService.getSyncedLyrics(title, artist)
    }
}

class AppUpNextProvider @Inject constructor(
    private val queueService: YouTubeQueueService,
) : UpNextProvider {
    override suspend fun upNext(trackId: String): List<Track> = queueService.getUpNextTracks(trackId)
}

/** Puente entre el player y la biblioteca del usuario (favoritos y descargas). */
class AppPlayerLibrary @Inject constructor(
    private val repository: TrackRepository,
    private val downloader: TrackDownloader,
    private val prefs: OnboardingPreferences,
) : PlayerLibrary {

    override fun status(trackId: String): Flow<TrackLibraryStatus> =
        repository.getAllTracks()
            .map { tracks ->
                val saved = tracks.find { it.trackId == trackId }
                TrackLibraryStatus(
                    isFavorite = saved?.isFavorite == true,
                    isDownloading = saved?.isDownloading == true,
                    localAudioPath = saved?.audioFilePath,
                )
            }
            .distinctUntilChanged()

    override suspend fun toggleFavorite(track: Track) {
        val wasFavorite = repository.isFavoriteTrack(track.id)
        repository.toggleFavorite(track)
        if (!wasFavorite && prefs.autoDownloadFavorites.first()) {
            val alreadyDownloaded = !repository.getTrackByVideoId(track.id)?.audioFilePath.isNullOrEmpty()
            if (!alreadyDownloaded) downloader.download(track)
        }
    }

    override suspend fun download(track: Track, streamUrl: String?) = downloader.download(track, streamUrl)
}

class AppPlayerSettings @Inject constructor(
    private val prefs: OnboardingPreferences,
) : PlayerSettings {
    override val showEqualizer: Flow<Boolean> = prefs.showEqualizer
    override suspend fun setShowEqualizer(show: Boolean) = prefs.setShowEqualizer(show)
}

class AppPlaybackObserver @Inject constructor(
    private val recentlyPlayed: RecentlyPlayedRepository,
) : PlaybackObserver {
    override suspend fun onTrackStarted(track: Track) = recentlyPlayed.save(track)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerBindings {
    @Binds abstract fun streamResolver(impl: AppStreamResolver): StreamResolver
    @Binds abstract fun lyricsProvider(impl: AppLyricsProvider): LyricsProvider
    @Binds abstract fun upNextProvider(impl: AppUpNextProvider): UpNextProvider
    @Binds abstract fun playerLibrary(impl: AppPlayerLibrary): PlayerLibrary
    @Binds abstract fun playerSettings(impl: AppPlayerSettings): PlayerSettings
    @Binds abstract fun playbackObserver(impl: AppPlaybackObserver): PlaybackObserver
}
