package com.davidsimba.vintbeats.feature.library.data.track

import com.davidsimba.vintbeats.feature.library.domain.track.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import com.davidsimba.vintbeats.core.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val dao: TrackDao
) : TrackRepository {

    override fun getAllTracks(): Flow<List<SavedTrack>> =
        dao.getAllTracks().map { it.map(SavedTrackEntity::toDomain) }

    override fun getDownloadedTracks(): Flow<List<SavedTrack>> =
        dao.getDownloadedTracks().map { it.map(SavedTrackEntity::toDomain) }

    override fun getFavoriteTracks(): Flow<List<SavedTrack>> =
        dao.getFavoriteTracks().map { it.map(SavedTrackEntity::toDomain) }

    override suspend fun getTrack(id: Int): SavedTrack? =
        dao.getById(id)?.toDomain()

    override suspend fun getTrackByVideoId(trackId: String): SavedTrack? =
        dao.getByTrackId(trackId)?.toDomain()

    override suspend fun saveTrack(track: Track, audioFilePath: String?) {
        val existing = dao.getByTrackId(track.id)
        dao.insert(
            SavedTrackEntity(
                id = existing?.id ?: 0,
                trackId = track.id,
                trackTitle = track.title,
                trackArtist = track.artist,
                trackThumbnailUrl = track.albumImageUrl,
                trackDurationText = track.durationText,
                savedAt = existing?.savedAt ?: System.currentTimeMillis(),
                audioFilePath = audioFilePath,
                isFavorite = existing?.isFavorite ?: false
            )
        )
    }

    override suspend fun deleteTrack(id: Int) = dao.deleteById(id)

    override suspend fun removeFavorite(id: Int) {
        val track = dao.getById(id) ?: return
        if (track.audioFilePath == null) {
            dao.deleteById(id)
        } else {
            dao.setFavorite(id, false)
        }
    }

    override suspend fun toggleFavorite(track: Track) {
        val existing = dao.getByTrackId(track.id)
        if (existing == null) {
            dao.insert(
                SavedTrackEntity(
                    trackId = track.id,
                    trackTitle = track.title,
                    trackArtist = track.artist,
                    trackThumbnailUrl = track.albumImageUrl,
                    trackDurationText = track.durationText,
                    isFavorite = true
                )
            )
        } else {
            val newFavorite = !existing.isFavorite
            if (!newFavorite && existing.audioFilePath == null) {
                dao.deleteById(existing.id)
            } else {
                dao.setFavorite(existing.id, newFavorite)
            }
        }
    }

    override suspend fun isFavoriteTrack(trackId: String): Boolean =
        dao.getByTrackId(trackId)?.isFavorite ?: false
}
