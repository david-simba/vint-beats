package com.davidsimba.vintbeats.core.model

/** Una canción elegida por el DJ. */
data class DjItem(val track: Track)

/**
 * Un set del DJ: texto de apertura ([intro]), canciones seguidas ([items]) y texto de cierre ([outro]).
 * [fallback] indica que la IA falló.
 */
data class DjSession(
    val intro: String,
    val items: List<DjItem>,
    val outro: String = "",
    val fallback: Boolean = false,
)
