package com.davidsimba.vintbeats.feature.home.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.feature.home.domain.RecentTrack

@Entity(
    tableName = "recently_played",
    indices = [Index(value = ["trackId"], unique = true)]
)
data class RecentlyPlayedEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackId: String,
    val title: String,
    val artist: String,
    val thumbnailUrl: String?,
    val playedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = RecentTrack(trackId, title, artist, thumbnailUrl)
}
