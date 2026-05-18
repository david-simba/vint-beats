package com.davidsimba.vintbeats.feature.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}