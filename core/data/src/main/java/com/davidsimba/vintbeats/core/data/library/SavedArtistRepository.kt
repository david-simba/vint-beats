package com.davidsimba.vintbeats.core.data.library

import kotlinx.coroutines.flow.Flow
import com.davidsimba.vintbeats.core.model.SavedArtist

interface SavedArtistRepository {
    fun getSavedArtists(): Flow<List<SavedArtist>>
    suspend fun saveArtist(artistId: String, name: String, thumbnailUrl: String?)
    suspend fun unsaveArtist(artistId: String)
    suspend fun isSaved(artistId: String): Boolean
}
