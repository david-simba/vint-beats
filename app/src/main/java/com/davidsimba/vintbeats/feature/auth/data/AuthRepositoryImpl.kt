package com.davidsimba.vintbeats.feature.auth.data

import com.davidsimba.vintbeats.core.datastore.TokenStorage
import com.davidsimba.vintbeats.feature.auth.domain.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val tokenStorage: TokenStorage
): AuthRepository {
    override val isLoggedIn: Flow<Boolean> = tokenStorage.accessToken.map {
        token -> token != null
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenStorage.saveToken(accessToken, refreshToken)
    }

    override suspend fun clearTokens() {
        tokenStorage.clearTokens()
    }
}