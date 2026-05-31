package com.davidsimba.vintbeats.feature.library.domain

import com.davidsimba.vintbeats.core.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getAllTracks(): Flow<List<SavedTrack>>
    suspend fun getTrack(id: Int): SavedTrack?
    suspend fun getTrackByVideoId(trackId: String): SavedTrack?
    suspend fun saveTrack(track: Track, audioFilePath: String?)
    suspend fun deleteTrack(id: Int)
}
