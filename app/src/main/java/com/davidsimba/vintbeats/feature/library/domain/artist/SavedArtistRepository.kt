package com.davidsimba.vintbeats.feature.library.domain.artist

import kotlinx.coroutines.flow.Flow

interface SavedArtistRepository {
    fun getSavedArtists(): Flow<List<SavedArtist>>
    suspend fun saveArtist(artistId: String, name: String, thumbnailUrl: String?)
    suspend fun unsaveArtist(artistId: String)
    suspend fun isSaved(artistId: String): Boolean
}
