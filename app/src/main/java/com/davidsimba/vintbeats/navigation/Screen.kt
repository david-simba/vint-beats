package com.davidsimba.vintbeats.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object SearchActive : Screen("search_active")
    data object Library : Screen("library")
    data object Player : Screen("player")
    data object Artist : Screen("artist/{browseId}") {
        fun route(browseId: String) = "artist/${Uri.encode(browseId)}"
    }
    data object Album : Screen("album/{browseId}") {
        fun route(browseId: String) = "album/${Uri.encode(browseId)}"
    }
    data object Playlist : Screen("playlist/{playlistId}") {
        fun route(playlistId: String) = "playlist/${Uri.encode(playlistId)}"
    }
    data object Favorites : Screen("favorites")
    data object Downloads : Screen("downloads")
}
