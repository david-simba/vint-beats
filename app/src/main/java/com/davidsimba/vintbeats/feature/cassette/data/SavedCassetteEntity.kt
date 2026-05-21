package com.davidsimba.vintbeats.feature.cassette.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette

@Entity(tableName = "saved_cassettes")
data class SavedCassetteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackId: String,
    val trackTitle: String,
    val trackArtist: String,
    val trackThumbnailUrl: String?,
    val trackDurationText: String,
    val cassetteColorArgb: Int,
    val lineColorArgb: Int,
    val isRainbow: Boolean,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = SavedCassette(
        id = id,
        trackId = trackId,
        trackTitle = trackTitle,
        trackArtist = trackArtist,
        trackThumbnailUrl = trackThumbnailUrl,
        trackDurationText = trackDurationText,
        cassetteColor = Color(cassetteColorArgb),
        lineColor = Color(lineColorArgb),
        isRainbow = isRainbow,
        savedAt = savedAt
    )
}
