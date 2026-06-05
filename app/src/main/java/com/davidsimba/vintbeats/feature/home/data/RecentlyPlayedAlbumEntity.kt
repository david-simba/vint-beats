package com.davidsimba.vintbeats.feature.home.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.feature.home.domain.RecentAlbum

@Entity(
    tableName = "recently_played_albums",
    indices = [Index(value = ["albumId"], unique = true)]
)
data class RecentlyPlayedAlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val albumId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val playedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = RecentAlbum(albumId, title, artist, thumbnailUrl)
}
