package com.davidsimba.vintbeats.core.model

data class Album(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val year: String?,
    /** "album", "single", "ep" o "collaboration". Null si el backend no lo envía. */
    val type: String? = null,
)
