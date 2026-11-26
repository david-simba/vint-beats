package com.davidsimba.vintbeats.core.data.library

import kotlinx.coroutines.flow.Flow
import com.davidsimba.vintbeats.core.model.SavedAlbum

interface SavedAlbumRepository {
    fun getSavedAlbums(): Flow<List<SavedAlbum>>
    suspend fun saveAlbum(albumId: String, title: String, artist: String, thumbnailUrl: String?)
    suspend fun unsaveAlbum(albumId: String)
    suspend fun isSaved(albumId: String): Boolean
}
