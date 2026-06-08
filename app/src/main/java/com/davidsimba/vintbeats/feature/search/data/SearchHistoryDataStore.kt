package com.davidsimba.vintbeats.feature.search.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.davidsimba.vintbeats.feature.search.domain.RecentSearch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history"
)

@Singleton
class SearchHistoryDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.searchHistoryDataStore
    private val gson = Gson()
    private val listType = object : TypeToken<List<RecentSearch>>() {}.type

    val items: Flow<List<RecentSearch>> = dataStore.data.map { prefs ->
        prefs[KEY_ITEMS]?.let { runCatching { gson.fromJson<List<RecentSearch>>(it, listType) }.getOrNull() } ?: emptyList()
    }

    suspend fun add(item: RecentSearch) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_ITEMS]
                ?.let { runCatching { gson.fromJson<List<RecentSearch>>(it, listType) }.getOrNull() }
                ?: emptyList()
            val updated = (listOf(item) + current.filter { it.id != item.id || it.type != item.type }).take(MAX_ENTRIES)
            prefs[KEY_ITEMS] = gson.toJson(updated)
        }
    }

    suspend fun remove(item: RecentSearch) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_ITEMS]
                ?.let { runCatching { gson.fromJson<List<RecentSearch>>(it, listType) }.getOrNull() }
                ?: emptyList()
            prefs[KEY_ITEMS] = gson.toJson(current.filter { it.id != item.id || it.type != item.type })
        }
    }

    suspend fun clear() {
        dataStore.edit { prefs -> prefs.remove(KEY_ITEMS) }
    }

    companion object {
        private val KEY_ITEMS = stringPreferencesKey("recent_items")
        private const val MAX_ENTRIES = 8
    }
}
