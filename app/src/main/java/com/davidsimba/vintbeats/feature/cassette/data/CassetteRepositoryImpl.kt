package com.davidsimba.vintbeats.feature.cassette.data

import androidx.compose.ui.graphics.toArgb
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteConfig
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteRepository
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CassetteRepositoryImpl @Inject constructor(
    private val dao: CassetteDao
) : CassetteRepository {

    override fun getAllCassettes(): Flow<List<SavedCassette>> =
        dao.getAllCassettes().map { it.map(SavedCassetteEntity::toDomain) }

    override suspend fun getCassette(id: Int): SavedCassette? =
        dao.getById(id)?.toDomain()

    override suspend fun saveCassette(config: CassetteConfig, audioFilePath: String?) {
        dao.insert(
            SavedCassetteEntity(
                trackId = config.track.id,
                trackTitle = config.track.title,
                trackArtist = config.track.artist,
                trackThumbnailUrl = config.track.albumImageUrl,
                trackDurationText = config.track.durationText,
                cassetteColorArgb = config.cassetteColor.toArgb(),
                lineColorArgb = config.lineColor.toArgb(),
                isRainbow = config.isRainbow,
                audioFilePath = audioFilePath
            )
        )
    }

    override suspend fun deleteCassette(id: Int) = dao.deleteById(id)
}
