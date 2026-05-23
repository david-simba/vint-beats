package com.davidsimba.vintbeats.feature.library.data

import com.davidsimba.vintbeats.feature.library.domain.SavedTrack
import com.davidsimba.vintbeats.feature.library.domain.TrackRepository
import com.davidsimba.vintbeats.core.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val dao: TrackDao
) : TrackRepository {

    override fun getAllTracks(): Flow<List<SavedTrack>> =
        dao.getAllTracks().map { it.map(SavedTrackEntity::toDomain) }

    override suspend fun getTrack(id: Int): SavedTrack? =
        dao.getById(id)?.toDomain()

    override suspend fun saveTrack(track: Track, audioFilePath: String?) {
        dao.insert(
            SavedTrackEntity(
                trackId = track.id,
                trackTitle = track.title,
                trackArtist = track.artist,
                trackThumbnailUrl = track.albumImageUrl,
                trackDurationText = track.durationText,
                audioFilePath = audioFilePath
            )
        )
    }

    override suspend fun deleteTrack(id: Int) = dao.deleteById(id)
}
