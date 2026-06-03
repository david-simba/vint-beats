package com.davidsimba.vintbeats.feature.library.domain.album

import kotlinx.coroutines.flow.Flow

interface SavedAlbumRepository {
    fun getSavedAlbums(): Flow<List<SavedAlbum>>
    suspend fun saveAlbum(albumId: String, title: String, artist: String, thumbnailUrl: String?)
    suspend fun unsaveAlbum(albumId: String)
    suspend fun isSaved(albumId: String): Boolean
}
