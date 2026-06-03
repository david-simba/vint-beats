package com.davidsimba.vintbeats.feature.library.domain.playlist

import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack

data class Playlist(
    val id: Int,
    val name: String,
    val trackCount: Int,
    val createdAt: Long,
    val coverImagePath: String? = null,
)

data class PlaylistInfo(val name: String, val coverImagePath: String?)

data class PlaylistWithTracks(
    val id: Int,
    val name: String,
    val tracks: List<SavedTrack>,
    val coverImagePath: String? = null,
)
