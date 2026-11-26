package com.davidsimba.vintbeats.feature.library.data

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

private val Context.libraryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "library_preferences",
)

@Singleton
class LibraryPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore: DataStore<Preferences> = context.libraryDataStore

    val isGridView: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_GRID_VIEW] ?: false
    }

    suspend fun setGridView(isGrid: Boolean) {
        dataStore.edit { prefs -> prefs[KEY_GRID_VIEW] = isGrid }
    }

    companion object {
        private val KEY_GRID_VIEW = booleanPreferencesKey("library_grid_view")
    }
}
