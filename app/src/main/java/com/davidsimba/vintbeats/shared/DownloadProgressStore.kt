package com.davidsimba.vintbeats.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadProgressStore @Inject constructor() {
    private val _progress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val progress: StateFlow<Map<String, Float>> = _progress.asStateFlow()

    fun set(trackId: String, value: Float) = _progress.update { it + (trackId to value) }
    fun remove(trackId: String) = _progress.update { it - trackId }
}
