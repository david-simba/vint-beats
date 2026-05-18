package com.davidsimba.vintbeats.core.datastore

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    suspend fun saveToken(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}