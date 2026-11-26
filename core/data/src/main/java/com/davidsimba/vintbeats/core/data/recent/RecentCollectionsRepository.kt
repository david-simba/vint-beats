package com.davidsimba.vintbeats.core.data.recent

import com.davidsimba.vintbeats.core.database.dao.RecentCollectionDao
import com.davidsimba.vintbeats.core.database.entity.RecentCollectionEntity
import com.davidsimba.vintbeats.core.model.RecentCollection
import com.davidsimba.vintbeats.core.model.RecentCollectionKind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentCollectionsRepository @Inject constructor(
    private val dao: RecentCollectionDao
) {
    fun getRecent(limit: Int = 10): Flow<List<RecentCollection>> =
        dao.getRecent(limit).map { list -> list.mapNotNull { it.toDomain() } }

    suspend fun save(
        kind: RecentCollectionKind,
        collectionId: String,
        title: String,
        thumbnailUrl: String?,
        artistId: String?,
        artistName: String?,
    ) {
        dao.upsert(
            RecentCollectionEntity(
                kind = kind.name,
                collectionId = collectionId,
                title = title,
                thumbnailUrl = thumbnailUrl,
                artistId = artistId,
                artistName = artistName,
            )
        )
    }
}
