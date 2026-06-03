package com.davidsimba.vintbeats.feature.library.domain

import com.davidsimba.vintbeats.core.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getAllTracks(): Flow<List<SavedTrack>>
    fun getDownloadedTracks(): Flow<List<SavedTrack>>
    fun getFavoriteTracks(): Flow<List<SavedTrack>>
    suspend fun getTrack(id: Int): SavedTrack?
    suspend fun getTrackByVideoId(trackId: String): SavedTrack?
    suspend fun saveTrack(track: Track, audioFilePath: String?)
    suspend fun deleteTrack(id: Int)
    suspend fun removeFavorite(id: Int)
    suspend fun toggleFavorite(track: Track)
    suspend fun isFavoriteTrack(trackId: String): Boolean
}
