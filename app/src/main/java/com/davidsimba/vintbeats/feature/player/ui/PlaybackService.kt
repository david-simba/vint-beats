package com.davidsimba.vintbeats.feature.player.ui

import android.content.Intent
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService : MediaSessionService() {

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()

        // ForwardingPlayer intercepts next/previous commands from the notification
        // and routes them to the ViewModel via PlaybackEventBus instead of the
        // internal playlist (which only ever has one item).
        val notificationPlayer = object : ForwardingPlayer(player) {
            override fun seekToNextMediaItem() = PlaybackEventBus.emit(PlaybackEvent.SkipNext)
            override fun seekToNext() = PlaybackEventBus.emit(PlaybackEvent.SkipNext)
            override fun seekToPreviousMediaItem() = PlaybackEventBus.emit(PlaybackEvent.SkipPrevious)
            override fun seekToPrevious() = PlaybackEventBus.emit(PlaybackEvent.SkipPrevious)

            // Always advertise next/previous as available so the notification shows the buttons.
            override fun getAvailableCommands(): Player.Commands =
                super.getAvailableCommands().buildUpon()
                    .add(Player.COMMAND_SEEK_TO_NEXT)
                    .add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .add(Player.COMMAND_SEEK_TO_PREVIOUS)
                    .add(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    .build()
        }

        // Emit SkipNext when a track ends naturally (auto-advance).
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    PlaybackEventBus.emit(PlaybackEvent.SkipNext)
                }
            }
        })

        mediaSession = MediaSession.Builder(this, notificationPlayer).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0) stopSelf()
    }

    override fun onDestroy() {
        mediaSession.release()
        player.release()
        super.onDestroy()
    }
}
