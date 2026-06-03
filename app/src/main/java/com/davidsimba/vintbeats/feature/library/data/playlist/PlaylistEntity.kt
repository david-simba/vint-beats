package com.davidsimba.vintbeats.feature.library.data.playlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val playlistId: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val coverImagePath: String? = null,
)
