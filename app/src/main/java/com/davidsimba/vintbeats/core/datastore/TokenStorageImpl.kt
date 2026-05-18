package com.davidsimba.vintbeats.core.datastore

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import androidx.core.content.edit

class TokenStorageImpl @Inject constructor(
    private val context: Context
): TokenStorage {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "vint_beats_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _accessToken = MutableStateFlow(
        encryptedPrefs.getString(TokenKeys.ACCESS_TOKEN, null)
    )

    private val _refreshToken = MutableStateFlow(
        encryptedPrefs.getString(TokenKeys.REFRESH_TOKEN, null)
    )

    override val accessToken: Flow<String?> = _accessToken

    override val refreshToken: Flow<String?> = _refreshToken

    override suspend fun saveToken(accessToken: String, refreshToken: String) {
        encryptedPrefs.edit {
            putString(TokenKeys.ACCESS_TOKEN, accessToken).putString(TokenKeys.REFRESH_TOKEN, refreshToken).apply()
            _accessToken.value = accessToken
            _refreshToken.value = refreshToken
        }
    }

    override suspend fun clearTokens() {
        encryptedPrefs.edit {
            remove(TokenKeys.ACCESS_TOKEN).remove(TokenKeys.REFRESH_TOKEN).apply()
        }
        _accessToken.value = null
        _refreshToken.value = null
    }
}