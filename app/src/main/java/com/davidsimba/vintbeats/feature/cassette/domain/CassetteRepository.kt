package com.davidsimba.vintbeats.feature.cassette.domain

import kotlinx.coroutines.flow.Flow

interface CassetteRepository {
    fun getAllCassettes(): Flow<List<SavedCassette>>
    suspend fun saveCassette(config: CassetteConfig)
    suspend fun deleteCassette(id: Int)
}
