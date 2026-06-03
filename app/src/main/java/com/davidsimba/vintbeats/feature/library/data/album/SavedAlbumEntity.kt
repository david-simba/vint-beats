package com.davidsimba.vintbeats.feature.library.data.album

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.feature.library.domain.album.SavedAlbum

@Entity(tableName = "saved_albums")
data class SavedAlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val albumId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = SavedAlbum(id, albumId, title, artist, thumbnailUrl, savedAt)
}
