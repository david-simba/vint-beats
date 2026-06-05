package com.davidsimba.vintbeats.feature.player.ui

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.davidsimba.vintbeats.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var cacheDataSourceFactory: CacheDataSource.Factory

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        val notificationPlayer = object : ForwardingPlayer(player) {
            override fun seekToNextMediaItem() = PlaybackEventBus.emit(PlaybackEvent.SkipNext)
            override fun seekToNext() = PlaybackEventBus.emit(PlaybackEvent.SkipNext)
            override fun seekToPreviousMediaItem() = PlaybackEventBus.emit(PlaybackEvent.SkipPrevious)
            override fun seekToPrevious() = PlaybackEventBus.emit(PlaybackEvent.SkipPrevious)

            override fun getAvailableCommands(): Player.Commands =
                super.getAvailableCommands().buildUpon()
                    .add(COMMAND_SEEK_TO_NEXT)
                    .add(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .add(COMMAND_SEEK_TO_PREVIOUS)
                    .add(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    .build()
        }

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    PlaybackEventBus.emit(PlaybackEvent.SkipNext)
                }
            }
        })

        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_OPEN_PLAYER, true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, notificationPlayer)
            .setSessionActivity(pendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    // Keep foreground service alive during track transitions (STATE_IDLE between setMediaItem→prepare)
    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        super.onUpdateNotification(
            session,
            startInForegroundRequired || session.player.currentMediaItem != null
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> if (player.isPlaying) player.pause() else player.play()
            ACTION_SKIP_NEXT -> PlaybackEventBus.emit(PlaybackEvent.SkipNext)
            ACTION_SKIP_PREVIOUS -> PlaybackEventBus.emit(PlaybackEvent.SkipPrevious)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        player.stop()
        stopSelf()
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.davidsimba.vintbeats.PLAY_PAUSE"
        const val ACTION_SKIP_NEXT = "com.davidsimba.vintbeats.SKIP_NEXT"
        const val ACTION_SKIP_PREVIOUS = "com.davidsimba.vintbeats.SKIP_PREVIOUS"
    }

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }
}
