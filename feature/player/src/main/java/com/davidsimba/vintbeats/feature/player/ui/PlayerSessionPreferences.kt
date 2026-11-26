package com.davidsimba.vintbeats.feature.player.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.playerDataStore: DataStore<Preferences> by preferencesDataStore("player_session")

data class LastTrackData(
    val trackId: String,
    val title: String,
    val artist: String,
    val thumbnail: String?,
)

@Singleton
class PlayerSessionPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.playerDataStore

    val lastTrack: Flow<LastTrackData?> = dataStore.data.map { prefs ->
        val trackId = prefs[KEY_TRACK_ID] ?: return@map null
        LastTrackData(
            trackId = trackId,
            title = prefs[KEY_TITLE] ?: return@map null,
            artist = prefs[KEY_ARTIST] ?: return@map null,
            thumbnail = prefs[KEY_THUMBNAIL]?.takeIf { it.isNotEmpty() },
        )
    }

    suspend fun save(
        trackId: String,
        title: String,
        artist: String,
        thumbnail: String?,
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_TRACK_ID] = trackId
            prefs[KEY_TITLE] = title
            prefs[KEY_ARTIST] = artist
            prefs[KEY_THUMBNAIL] = thumbnail ?: ""
        }
    }

    companion object {
        private val KEY_TRACK_ID  = stringPreferencesKey("track_id")
        private val KEY_TITLE     = stringPreferencesKey("title")
        private val KEY_ARTIST    = stringPreferencesKey("artist")
        private val KEY_THUMBNAIL = stringPreferencesKey("thumbnail")
    }
}
