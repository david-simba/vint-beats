package com.davidsimba.vintbeats.core.model

/** Una canción del DJ junto con la frase que dice antes de ponerla. */
data class DjItem(val track: Track, val say: String)

/** Respuesta del DJ: presentación inicial y canciones con su frase. [fallback] indica que la IA falló. */
data class DjSession(val intro: String, val items: List<DjItem>, val fallback: Boolean = false)
