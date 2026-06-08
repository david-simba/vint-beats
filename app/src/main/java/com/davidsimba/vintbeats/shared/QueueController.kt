package com.davidsimba.vintbeats.shared

import com.davidsimba.vintbeats.core.model.Track
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object QueueController {
    private val _events = MutableSharedFlow<Track>(extraBufferCapacity = 1)
    val events: SharedFlow<Track> = _events.asSharedFlow()

    fun addToQueue(track: Track) {
        _events.tryEmit(track)
    }
}
