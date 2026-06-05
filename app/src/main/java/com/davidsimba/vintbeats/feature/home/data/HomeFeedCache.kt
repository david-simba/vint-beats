package com.davidsimba.vintbeats.feature.home.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.davidsimba.vintbeats.core.model.Track
import com.davidsimba.vintbeats.feature.home.domain.ArtistRadioItem
import com.davidsimba.vintbeats.feature.home.domain.HomeSectionPlaylists
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.homeFeedDataStore by preferencesDataStore("home_feed_cache")

data class CachedHomeFeed(
    val quickMix: List<Track>,
    val extraSections: List<HomeSectionPlaylists>,
    val artistRadios: List<ArtistRadioItem>,
)

@Singleton
class HomeFeedCache @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.homeFeedDataStore
    private val gson = Gson()

    suspend fun get(): CachedHomeFeed? {
        val prefs = dataStore.data.first()
        val cachedDate = prefs[KEY_DATE] ?: return null
        if (cachedDate != LocalDate.now().toString()) return null

        return try {
            CachedHomeFeed(
                quickMix = gson.fromJson(prefs[KEY_QUICK_MIX] ?: return null, trackListType),
                extraSections = gson.fromJson(prefs[KEY_EXTRA_SECTIONS] ?: return null, extraSectionsType),
                artistRadios = gson.fromJson(prefs[KEY_ARTIST_RADIOS] ?: return null, artistRadiosType),
            )
        } catch (_: Exception) {
            null
        }
    }

    suspend fun save(
        quickMix: List<Track>,
        extraSections: List<HomeSectionPlaylists>,
        artistRadios: List<ArtistRadioItem>,
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_DATE] = LocalDate.now().toString()
            prefs[KEY_QUICK_MIX] = gson.toJson(quickMix)
            prefs[KEY_EXTRA_SECTIONS] = gson.toJson(extraSections)
            prefs[KEY_ARTIST_RADIOS] = gson.toJson(artistRadios)
        }
    }

    companion object {
        private val KEY_DATE = stringPreferencesKey("date")
        private val KEY_QUICK_MIX = stringPreferencesKey("quick_mix")
        private val KEY_EXTRA_SECTIONS = stringPreferencesKey("extra_sections")
        private val KEY_ARTIST_RADIOS = stringPreferencesKey("artist_radios")

        private val trackListType = object : TypeToken<List<Track>>() {}.type
        private val extraSectionsType = object : TypeToken<List<HomeSectionPlaylists>>() {}.type
        private val artistRadiosType = object : TypeToken<List<ArtistRadioItem>>() {}.type
    }
}
