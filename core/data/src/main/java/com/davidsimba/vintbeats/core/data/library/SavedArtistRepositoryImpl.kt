package com.davidsimba.vintbeats.core.data.library

import com.davidsimba.vintbeats.core.model.SavedArtist
import com.davidsimba.vintbeats.core.data.library.SavedArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.davidsimba.vintbeats.core.database.dao.SavedArtistDao
import com.davidsimba.vintbeats.core.database.entity.SavedArtistEntity

class SavedArtistRepositoryImpl @Inject constructor(
    private val dao: SavedArtistDao
) : SavedArtistRepository {

    override fun getSavedArtists(): Flow<List<SavedArtist>> =
        dao.getAll().map { it.map(SavedArtistEntity::toDomain) }

    override suspend fun saveArtist(artistId: String, name: String, thumbnailUrl: String?) {
        dao.insert(SavedArtistEntity(artistId = artistId, name = name, thumbnailUrl = thumbnailUrl))
    }

    override suspend fun unsaveArtist(artistId: String) = dao.deleteByArtistId(artistId)

    override suspend fun isSaved(artistId: String): Boolean = dao.countByArtistId(artistId) > 0
}
