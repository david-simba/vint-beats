package com.davidsimba.vintbeats.core.data.download

import android.content.Context
import android.util.Log
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.core.data.library.TrackRepository
import com.davidsimba.vintbeats.core.data.stream.YouTubeStreamService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.davidsimba.vintbeats.core.common.SnackbarController
import com.davidsimba.vintbeats.core.common.SnackbarEvent

/**
 * Descarga una canción al almacenamiento local y actualiza la biblioteca.
 * Único lugar con esta lógica: lo usan las acciones de track y el player.
 */
@Singleton
class TrackDownloader @Inject constructor(
    private val repository: TrackRepository,
    private val streamService: YouTubeStreamService,
    private val progressStore: DownloadProgressStore,
    @ApplicationContext private val context: Context,
) {
    /** [streamUrl] opcional: si ya se resolvió, se reutiliza. */
    suspend fun download(track: Track, streamUrl: String? = null) {
        if (repository.getTrackByVideoId(track.id)?.isDownloading == true) return
        try {
            repository.startDownload(track)
            SnackbarController.emit(SnackbarEvent.DownloadStarted)
            val url = streamUrl ?: streamService.getAudioStreamUrl(track.id)
            val audioFilePath = streamService.downloadAudio(
                videoId = track.id,
                streamUrl = url,
                destDir = context.filesDir,
                onProgress = { progressStore.set(track.id, it) }
            )
            progressStore.remove(track.id)
            repository.finishDownload(track.id, audioFilePath)
            if (audioFilePath == null) SnackbarController.emit(SnackbarEvent.DownloadError)
        } catch (e: Exception) {
            Log.e("TrackDownloader", "[${track.id}] download failed: ${e::class.simpleName}: ${e.message}", e)
            progressStore.remove(track.id)
            repository.finishDownload(track.id, null)
            SnackbarController.emit(SnackbarEvent.DownloadError)
        }
    }
}
