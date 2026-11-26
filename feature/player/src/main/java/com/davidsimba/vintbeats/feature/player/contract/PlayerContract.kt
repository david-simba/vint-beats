package com.davidsimba.vintbeats.feature.player.contract

import com.davidsimba.vintbeats.core.model.LyricLine
import com.davidsimba.vintbeats.core.model.Track
import kotlinx.coroutines.flow.Flow

/*
 * Contrato público del player.
 *
 * El player solo conoce `Track`. Todo lo que viene de fuera (de dónde sale el audio,
 * las letras, la cola, el estado de favorito/descarga, los ajustes) entra por estas
 * interfaces, que implementa quien aloja al player (:app).
 */

/** Resuelve de dónde se reproduce una canción. */
interface StreamResolver {
    /** URL reproducible para [trackId]. */
    suspend fun resolve(trackId: String): String

    /** Precalienta la fuente de una canción cercana en la cola. No es crítico. */
    suspend fun prefetch(trackId: String)
}

interface LyricsProvider {
    suspend fun syncedLyrics(title: String, artist: String): List<LyricLine>
}

/** Canciones sugeridas a continuación a partir de una canción. */
interface UpNextProvider {
    suspend fun upNext(trackId: String): List<Track>
}

/** Lo que la biblioteca sabe de una canción y el player necesita mostrar o usar. */
data class TrackLibraryStatus(
    val isFavorite: Boolean = false,
    val isDownloading: Boolean = false,
    val localAudioPath: String? = null,
) {
    val hasLocalFile: Boolean get() = !localAudioPath.isNullOrEmpty()
}

interface PlayerLibrary {
    /** Estado reactivo de [trackId]: cambia cuando se marca favorito o termina una descarga. */
    fun status(trackId: String): Flow<TrackLibraryStatus>

    suspend fun toggleFavorite(track: Track)

    /** [streamUrl] es la URL ya resuelta, si el player la tiene; evita resolverla otra vez. */
    suspend fun download(track: Track, streamUrl: String?)
}

interface PlayerSettings {
    val showEqualizer: Flow<Boolean>
    suspend fun setShowEqualizer(show: Boolean)

    /** false (por defecto): portada como cuadro sobre el player; true: portada a pantalla completa de fondo. */
    val coverAsBackground: Flow<Boolean>
    suspend fun setCoverAsBackground(asBackground: Boolean)
}

/** Avisa al host de que empezó una canción (historial reciente, etc.). */
fun interface PlaybackObserver {
    suspend fun onTrackStarted(track: Track)
}
