package com.davidsimba.vintbeats.core.data.library

import com.davidsimba.vintbeats.core.model.SavedAlbum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.davidsimba.vintbeats.core.database.dao.SavedAlbumDao
import com.davidsimba.vintbeats.core.database.entity.SavedAlbumEntity

class SavedAlbumRepositoryImpl @Inject constructor(
    private val dao: SavedAlbumDao
) : SavedAlbumRepository {

    override fun getSavedAlbums(): Flow<List<SavedAlbum>> =
        dao.getAll().map { it.map(SavedAlbumEntity::toDomain) }

    override suspend fun saveAlbum(albumId: String, title: String, artist: String, thumbnailUrl: String?) {
        dao.insert(SavedAlbumEntity(albumId = albumId, title = title, artist = artist, thumbnailUrl = thumbnailUrl))
    }

    override suspend fun unsaveAlbum(albumId: String) = dao.deleteByAlbumId(albumId)

    override suspend fun isSaved(albumId: String): Boolean = dao.countByAlbumId(albumId) > 0
}
