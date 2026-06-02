package com.davidsimba.vintbeats.feature.library.domain

data class Playlist(
    val id: Int,
    val name: String,
    val trackCount: Int,
    val createdAt: Long,
    val coverImagePath: String? = null,
)

data class PlaylistWithTracks(
    val id: Int,
    val name: String,
    val tracks: List<SavedTrack>,
    val coverImagePath: String? = null,
)
