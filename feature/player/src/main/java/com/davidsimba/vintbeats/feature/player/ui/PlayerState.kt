package com.davidsimba.vintbeats.feature.player.ui

sealed interface PlayerState {
    data object Idle : PlayerState
    data object Loading : PlayerState
    data object Playing : PlayerState
    data class Error(val message: String) : PlayerState
}
