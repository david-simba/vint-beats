package com.davidsimba.vintbeats.feature.library.data

import com.davidsimba.vintbeats.feature.library.domain.Playlist
import com.davidsimba.vintbeats.feature.library.domain.PlaylistRepository
import com.davidsimba.vintbeats.feature.library.domain.PlaylistWithTracks
import com.davidsimba.vintbeats.feature.library.domain.SavedTrack
import kotlinx.coroutines.flow.Flow
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
        dao.getById(playlistId).map { it?.toDetailDomain() }

    override suspend fun createPlaylist(name: String, coverImagePath: String?): Int =
        dao.insert(PlaylistEntity(name = name, coverImagePath = coverImagePath)).toInt()

    override suspend fun deletePlaylist(playlistId: Int) =
        dao.deleteById(playlistId)

    override suspend fun addTrackToPlaylist(playlistId: Int, savedTrackId: Int) =
        dao.addTrack(PlaylistTrackCrossRef(playlistId, savedTrackId))

    override suspend fun removeTrackFromPlaylist(playlistId: Int, savedTrackId: Int) =
        dao.removeTrack(PlaylistTrackCrossRef(playlistId, savedTrackId))
}

private fun PlaylistWithTracksEntity.toDomain() = Playlist(
    id = playlist.playlistId,
    name = playlist.name,
    trackCount = tracks.size,
    createdAt = playlist.createdAt,
    coverImagePath = playlist.coverImagePath,
)

private fun PlaylistWithTracksEntity.toDetailDomain() = PlaylistWithTracks(
    id = playlist.playlistId,
    name = playlist.name,
    tracks = tracks.map(SavedTrackEntity::toDomain),
    coverImagePath = playlist.coverImagePath,
)

private fun SavedTrackEntity.toDomain() = SavedTrack(
    id = id,
    trackId = trackId,
    trackTitle = trackTitle,
    trackArtist = trackArtist,
    trackThumbnailUrl = trackThumbnailUrl,
    trackDurationText = trackDurationText,
    savedAt = savedAt,
    audioFilePath = audioFilePath,
    isFavorite = isFavorite,
)
