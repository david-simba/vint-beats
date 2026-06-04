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
    data object Playlist : Screen("playlist/{playlistId}?thumbnailUrl={thumbnailUrl}&artistId={artistId}&artistName={artistName}") {
        fun route(
            playlistId: String,
            thumbnailUrl: String? = null,
            artistId: String? = null,
            artistName: String? = null,
        ): String {
            val base = "playlist/${Uri.encode(playlistId)}"
            val params = buildList {
                if (thumbnailUrl != null) add("thumbnailUrl=${Uri.encode(thumbnailUrl)}")
                if (artistId != null) add("artistId=${Uri.encode(artistId)}")
                if (artistName != null) add("artistName=${Uri.encode(artistName)}")
            }
            return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
        }
    }
    data object Favorites : Screen("favorites")
    data object Downloads : Screen("downloads")
    data object CreatePlaylist : Screen("create_playlist?playlistId={playlistId}") {
        fun route(playlistId: Int? = null) =
            if (playlistId != null) "create_playlist?playlistId=$playlistId" else "create_playlist"
    }
    data object UserPlaylist : Screen("user_playlist/{playlistId}") {
        fun route(playlistId: Int) = "user_playlist/$playlistId"
    }
    data object AddSongs : Screen("add_songs/{playlistId}") {
        fun route(playlistId: Int) = "add_songs/$playlistId"
    }
    data object EditPlaylist : Screen("edit_playlist/{playlistId}") {
        fun route(playlistId: Int) = "edit_playlist/$playlistId"
    }
    data object Onboarding : Screen("onboarding")
}
