package com.davidsimba.vintbeats.feature.player.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PlayerPreferences {
    private val _equalizerEnabled = MutableStateFlow(false)
    val equalizerEnabled = _equalizerEnabled.asStateFlow()

    fun toggleEqualizer() {
        _equalizerEnabled.value = !_equalizerEnabled.value
    }
}
