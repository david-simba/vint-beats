package com.davidsimba.vintbeats.feature.library.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.feature.library.domain.SavedTrack

@Entity(tableName = "saved_tracks")
data class SavedTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackId: String,
    val trackTitle: String,
    val trackArtist: String,
    val trackThumbnailUrl: String?,
    val trackDurationText: String,
    val savedAt: Long = System.currentTimeMillis(),
    val audioFilePath: String? = null
) {
    fun toDomain() = SavedTrack(
        id = id,
        trackId = trackId,
        trackTitle = trackTitle,
        trackArtist = trackArtist,
        trackThumbnailUrl = trackThumbnailUrl,
        trackDurationText = trackDurationText,
        savedAt = savedAt,
        audioFilePath = audioFilePath
    )
}
