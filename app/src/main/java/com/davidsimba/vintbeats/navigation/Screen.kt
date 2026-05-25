package com.davidsimba.vintbeats.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Library : Screen("library")
    data object Player : Screen("player")
    data object Artist : Screen("artist/{browseId}") {
        fun route(browseId: String) = "artist/${Uri.encode(browseId)}"
    }
}
