package com.davidsimba.vintbeats.feature.player.ui

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class PlaybackEvent {
    object SkipNext : PlaybackEvent()
    object SkipPrevious : PlaybackEvent()
}

object PlaybackEventBus {
    private val _events = MutableSharedFlow<PlaybackEvent>(
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.asSharedFlow()

    fun emit(event: PlaybackEvent) {
        _events.tryEmit(event)
    }
}
