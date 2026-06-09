package com.davidsimba.vintbeats.feature.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore("onboarding")

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.onboardingDataStore

    val isComplete: Flow<Boolean> = dataStore.data.map { it[KEY_COMPLETE] ?: false }
    val userName: Flow<String> = dataStore.data.map { it[KEY_NAME] ?: "" }
    val photoUri: Flow<String?> = dataStore.data.map { it[KEY_PHOTO_URI] }
    val photoVersion: Flow<Long> = dataStore.data.map { it[KEY_PHOTO_VERSION] ?: 0L }
    val autoDownloadFavorites: Flow<Boolean> = dataStore.data.map { it[KEY_AUTO_DOWNLOAD_FAVORITES] ?: false }
    val showEqualizer: Flow<Boolean> = dataStore.data.map { it[KEY_SHOW_EQUALIZER] ?: false }

    suspend fun setComplete(complete: Boolean) {
        dataStore.edit { it[KEY_COMPLETE] = complete }
    }

    suspend fun setName(name: String) {
        dataStore.edit { it[KEY_NAME] = name }
    }

    suspend fun setPhotoUri(uri: String?) {
        dataStore.edit { prefs ->
            if (uri != null) prefs[KEY_PHOTO_URI] = uri
            else prefs.remove(KEY_PHOTO_URI)
            prefs[KEY_PHOTO_VERSION] = System.currentTimeMillis()
        }
    }

    suspend fun setAutoDownloadFavorites(enabled: Boolean) {
        dataStore.edit { it[KEY_AUTO_DOWNLOAD_FAVORITES] = enabled }
    }

    suspend fun setShowEqualizer(enabled: Boolean) {
        dataStore.edit { it[KEY_SHOW_EQUALIZER] = enabled }
    }

    companion object {
        private val KEY_COMPLETE = booleanPreferencesKey("onboarding_complete")
        private val KEY_NAME = stringPreferencesKey("user_name")
        private val KEY_PHOTO_URI = stringPreferencesKey("photo_uri")
        private val KEY_PHOTO_VERSION = longPreferencesKey("photo_version")
        private val KEY_AUTO_DOWNLOAD_FAVORITES = booleanPreferencesKey("auto_download_favorites")
        private val KEY_SHOW_EQUALIZER = booleanPreferencesKey("show_equalizer")
    }
}
