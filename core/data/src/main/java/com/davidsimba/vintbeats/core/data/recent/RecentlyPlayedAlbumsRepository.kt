package com.davidsimba.vintbeats.core.data.recent

import com.davidsimba.vintbeats.core.model.AlbumDetail
import com.davidsimba.vintbeats.core.model.RecentAlbum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.davidsimba.vintbeats.core.database.dao.RecentlyPlayedAlbumDao
import com.davidsimba.vintbeats.core.database.entity.RecentlyPlayedAlbumEntity

@Singleton
class RecentlyPlayedAlbumsRepository @Inject constructor(
    private val dao: RecentlyPlayedAlbumDao
) {
    fun getRecent(limit: Int = 10): Flow<List<RecentAlbum>> =
        dao.getRecent(limit).map { list -> list.map { it.toDomain() } }

    suspend fun save(album: AlbumDetail) {
        dao.upsert(
            RecentlyPlayedAlbumEntity(
                albumId = album.id,
                title = album.title,
                artist = album.artist,
                thumbnailUrl = album.thumbnailUrl,
            )
        )
    }
}
