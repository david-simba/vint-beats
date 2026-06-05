package com.davidsimba.vintbeats.feature.player.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.davidsimba.vintbeats.feature.player.ui.PlaybackService

class PlayPauseCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.startService(Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_PLAY_PAUSE
        })
    }
}

class SkipNextCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.startService(Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_SKIP_NEXT
        })
    }
}

class SkipPreviousCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.startService(Intent(context, PlaybackService::class.java).apply {
            action = PlaybackService.ACTION_SKIP_PREVIOUS
        })
    }
}
