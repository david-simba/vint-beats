package com.davidsimba.vintbeats.feature.player.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NowPlayingWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) {
    companion object {
        val KEY_TITLE = stringPreferencesKey("widget_title")
        val KEY_ARTIST = stringPreferencesKey("widget_artist")
        val KEY_IS_PLAYING = booleanPreferencesKey("widget_is_playing")
        val KEY_HAS_TRACK = booleanPreferencesKey("widget_has_track")
        val KEY_COVER_PATH = stringPreferencesKey("widget_cover_path")
    }

    suspend fun update(
        title: String,
        artist: String,
        thumbnailUrl: String?,
        isPlaying: Boolean,
    ) {
        val glanceId = NowPlayingWidget.glanceId(context) ?: return
        val coverPath = thumbnailUrl?.let { downloadCover(it) }

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[KEY_TITLE] = title
                this[KEY_ARTIST] = artist
                this[KEY_IS_PLAYING] = isPlaying
                this[KEY_HAS_TRACK] = true
                if (coverPath != null) this[KEY_COVER_PATH] = coverPath
            }
        }
        NowPlayingWidget().updateAll(context)
    }

    suspend fun clear() {
        val glanceId = NowPlayingWidget.glanceId(context) ?: return
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[KEY_HAS_TRACK] = false
            }
        }
        NowPlayingWidget().updateAll(context)
    }

    private suspend fun downloadCover(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
            val result = imageLoader.execute(request)
            if (result !is SuccessResult) return@withContext null
            val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                ?: return@withContext null
            val file = File(context.filesDir, "widget_cover.png")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 90, it) }
            file.absolutePath
        } catch (_: Exception) { null }
    }
}
