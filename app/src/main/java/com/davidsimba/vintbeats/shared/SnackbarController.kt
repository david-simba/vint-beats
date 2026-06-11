package com.davidsimba.vintbeats.shared

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class SnackbarEvent {
    object DownloadStarted : SnackbarEvent()
    object DownloadSuccess : SnackbarEvent()
    object DownloadError : SnackbarEvent()
}

object SnackbarController {
    private val _events = MutableSharedFlow<SnackbarEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SnackbarEvent> = _events.asSharedFlow()

    fun emit(event: SnackbarEvent) {
        _events.tryEmit(event)
    }
}
