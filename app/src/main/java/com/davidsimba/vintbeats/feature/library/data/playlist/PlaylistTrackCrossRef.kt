package com.davidsimba.vintbeats.feature.library.data.playlist

import androidx.room.Entity

@Entity(
    tableName = "playlist_track_cross_ref",
    primaryKeys = ["playlistId", "savedTrackId"],
)
data class PlaylistTrackCrossRef(
    val playlistId: Int,
    val savedTrackId: Int,
    val addedAt: Long = System.currentTimeMillis(),
    val displayOrder: Int = 0,
)
