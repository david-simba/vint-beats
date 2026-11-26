package com.davidsimba.vintbeats.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.core.model.SavedArtist

@Entity(tableName = "saved_artists")
data class SavedArtistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val artistId: String,
    val name: String,
    val thumbnailUrl: String?,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = SavedArtist(id, artistId, name, thumbnailUrl, savedAt)
}
