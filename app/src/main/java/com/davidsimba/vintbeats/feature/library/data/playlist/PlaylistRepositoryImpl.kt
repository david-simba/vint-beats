package com.davidsimba.vintbeats.feature.library.data.playlist

import com.davidsimba.vintbeats.feature.library.domain.playlist.Playlist
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistInfo
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistWithTracks
import com.davidsimba.vintbeats.feature.library.data.track.SavedTrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val dao: PlaylistDao,
) : PlaylistRepository {

    override fun getPlaylists(): Flow<List<Playlist>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getPlaylistWithTracks(playlistId: Int): Flow<PlaylistWithTracks?> =
        combine(dao.getById(playlistId), dao.getOrderedTracks(playlistId)) { entity, tracks ->
            entity ?: return@combine null
            PlaylistWithTracks(
                id = entity.playlist.playlistId,
                name = entity.playlist.name,
                tracks = tracks.map(SavedTrackEntity::toDomain),
                coverImagePath = entity.playlist.coverImagePath,
            )
        }

    override suspend fun getPlaylistInfo(playlistId: Int): PlaylistInfo? =
        dao.getPlaylistInfo(playlistId)?.let { PlaylistInfo(it.name, it.coverImagePath) }

    override suspend fun createPlaylist(name: String, coverImagePath: String?): Int =
        dao.insert(PlaylistEntity(name = name, coverImagePath = coverImagePath)).toInt()

    override suspend fun updatePlaylist(playlistId: Int, name: String, coverImagePath: String?) =
        dao.updatePlaylist(playlistId, name, coverImagePath)

    override suspend fun deletePlaylist(playlistId: Int) =
        dao.deleteById(playlistId)

    override suspend fun addTrackToPlaylist(playlistId: Int, savedTrackId: Int) {
        val order = dao.nextDisplayOrder(playlistId)
        dao.addTrack(PlaylistTrackCrossRef(playlistId, savedTrackId, displayOrder = order))
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, savedTrackId: Int) =
        dao.removeTrack(PlaylistTrackCrossRef(playlistId, savedTrackId))

    override suspend fun reorderTracks(playlistId: Int, orderedTrackIds: List<Int>) {
        orderedTrackIds.forEachIndexed { index, trackId ->
            dao.updateTrackOrder(playlistId, trackId, index)
        }
    }
}

private fun PlaylistWithTracksEntity.toDomain() = Playlist(
    id = playlist.playlistId,
    name = playlist.name,
    trackCount = tracks.size,
    createdAt = playlist.createdAt,
    coverImagePath = playlist.coverImagePath,
)
