package com.davidsimba.vintbeats.player

import com.davidsimba.vintbeats.core.model.SavedTrack
import com.davidsimba.vintbeats.core.model.toTrack
import com.davidsimba.vintbeats.feature.player.ui.PlaybackViewModel

/**
 * Reproduce una canción de la biblioteca. El player solo entiende `Track`, así que
 * el host traduce aquí: [tracks] es el contexto (lista, playlist, descargas…) y el resto
 * de ese contexto pasa a ser la cola.
 */
fun PlaybackViewModel.playSaved(savedTrackId: Int, tracks: List<SavedTrack>) {
    val selected = tracks.find { it.id == savedTrackId } ?: return
    playTrack(
        selected.toTrack(),
        newQueue = tracks.filter { it.id != savedTrackId }.map { it.toTrack() }
    )
}
