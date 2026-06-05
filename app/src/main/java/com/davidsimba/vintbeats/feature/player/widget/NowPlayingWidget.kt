package com.davidsimba.vintbeats.feature.player.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import com.davidsimba.vintbeats.MainActivity
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import com.davidsimba.vintbeats.R
import java.io.File

@SuppressLint("RestrictedApi")
class NowPlayingWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { WidgetContent() }
    }

    @Composable
    private fun WidgetContent() {
        val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
        val hasTrack = prefs[NowPlayingWidgetUpdater.KEY_HAS_TRACK] ?: false
        val title = prefs[NowPlayingWidgetUpdater.KEY_TITLE] ?: ""
        val artist = prefs[NowPlayingWidgetUpdater.KEY_ARTIST] ?: ""
        val isPlaying = prefs[NowPlayingWidgetUpdater.KEY_IS_PLAYING] ?: false
        val coverPath = prefs[NowPlayingWidgetUpdater.KEY_COVER_PATH]
        val context = LocalContext.current
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        GlanceTheme {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(R.color.widget_bg))
                    .cornerRadius(16.dp)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .clickable(actionStartActivity(openIntent)),
                contentAlignment = Alignment.Center
            ) {
                if (!hasTrack) {
                    EmptyState()
                } else {
                    TrackRow(
                        title = title,
                        artist = artist,
                        isPlaying = isPlaying,
                        coverPath = coverPath
                    )
                }
            }
        }
    }

    @Composable
    private fun EmptyState() {
        Text(
            text = "No hay nada reproduciéndose",
            style = TextDefaults.defaultTextStyle.copy(
                color = ColorProvider(R.color.widget_text_secondary),
                fontSize = 12.sp
            )
        )
    }

    @Composable
    private fun TrackRow(
        title: String,
        artist: String,
        isPlaying: Boolean,
        coverPath: String?,
    ) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val coverBitmap = coverPath?.let {
                val file = File(it)
                if (file.exists()) BitmapFactory.decodeFile(it) else null
            }
            Box(
                modifier = GlanceModifier
                    .size(46.dp)
                    .cornerRadius(6.dp)
                    .background(ColorProvider(R.color.widget_surface))
            ) {
                if (coverBitmap != null) {
                    Image(
                        provider = ImageProvider(coverBitmap),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = GlanceModifier.fillMaxSize()
                    )
                } else {
                    Image(
                        provider = ImageProvider(R.drawable.ic_music_note),
                        contentDescription = null,
                        modifier = GlanceModifier.size(22.dp).padding(12.dp)
                    )
                }
            }

            Spacer(GlanceModifier.width(10.dp))

            Column(
                modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = TextDefaults.defaultTextStyle.copy(
                        color = ColorProvider(R.color.widget_text_primary),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = artist,
                    maxLines = 1,
                    style = TextDefaults.defaultTextStyle.copy(
                        color = ColorProvider(R.color.widget_text_secondary),
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(GlanceModifier.width(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ControlButton(R.drawable.ic_skip_previous, "Anterior", actionRunCallback<SkipPreviousCallback>())
                ControlButton(
                    if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                    if (isPlaying) "Pausar" else "Reproducir",
                    actionRunCallback<PlayPauseCallback>()
                )
                ControlButton(R.drawable.ic_skip_next, "Siguiente", actionRunCallback<SkipNextCallback>())
            }
        }
    }

    @Composable
    private fun ControlButton(
        iconRes: Int,
        contentDescription: String,
        action: androidx.glance.action.Action,
    ) {
        Box(
            modifier = GlanceModifier
                .size(42.dp)
                .cornerRadius(21.dp)
                .clickable(action),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(iconRes),
                contentDescription = contentDescription,
                modifier = GlanceModifier.size(24.dp)
            )
        }
    }

    companion object {
        suspend fun glanceId(context: Context): GlanceId? =
            GlanceAppWidgetManager(context).getGlanceIds(NowPlayingWidget::class.java).firstOrNull()
    }
}
