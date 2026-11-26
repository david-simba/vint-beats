package com.davidsimba.vintbeats.feature.player.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NowPlayingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = NowPlayingWidget()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // Reset isPlaying when widget is updated by system (boot/restore) — app may not be running
        CoroutineScope(Dispatchers.IO).launch {
            val glanceId = GlanceAppWidgetManager(context)
                .getGlanceIds(NowPlayingWidget::class.java).firstOrNull() ?: return@launch
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[NowPlayingWidgetUpdater.KEY_IS_PLAYING] = false
                }
            }
            NowPlayingWidget().updateAll(context)
        }
    }
}
