package com.davidsimba.vintbeats.feature.onboarding

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

    suspend fun setComplete(complete: Boolean) {
        dataStore.edit { it[KEY_COMPLETE] = complete }
    }

    companion object {
        private val KEY_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }
}
