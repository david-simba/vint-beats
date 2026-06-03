package com.davidsimba.vintbeats.feature.library.data.artist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.feature.library.domain.artist.SavedArtist

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
