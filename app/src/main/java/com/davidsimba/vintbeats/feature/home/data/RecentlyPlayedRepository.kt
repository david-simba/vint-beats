package com.davidsimba.vintbeats.feature.home.data

import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.home.domain.RecentTrack
import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentlyPlayedRepository @Inject constructor(
    private val dao: RecentlyPlayedDao
) {
    fun getRecent(limit: Int = 10): Flow<List<RecentTrack>> =
        dao.getRecent(limit).map { list -> list.map { it.toDomain() } }

    suspend fun save(track: Track) {
        dao.upsert(
            RecentlyPlayedEntity(
                trackId = track.id,
                title = track.title,
                artist = track.artist,
                thumbnailUrl = track.albumImageUrl,
            )
        )
    }

    suspend fun save(track: SavedTrack) {
        dao.upsert(
            RecentlyPlayedEntity(
                trackId = track.trackId,
                title = track.trackTitle,
                artist = track.trackArtist,
                thumbnailUrl = track.trackThumbnailUrl,
            )
        )
    }
}
