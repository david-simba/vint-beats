package com.davidsimba.vintbeats.feature.home.domain

import androidx.compose.runtime.Immutable

@Immutable
sealed interface QuickAccessItem {
    val key: String
    val title: String?
    val thumbnailUrl: String?

    /** "Canciones que te gustan": el título sale de strings.xml, por eso es null. */
    data object Favorites : QuickAccessItem {
        override val key = "favorites"
        override val title: String? = null
        override val thumbnailUrl: String? = null
    }

    data class Album(val albumId: String, override val title: String, override val thumbnailUrl: String?) : QuickAccessItem {
        override val key = "album:$albumId"
    }

    data class Playlist(
        val playlistId: String,
        override val title: String,
        override val thumbnailUrl: String?,
        val artistId: String?,
        val artistName: String?,
    ) : QuickAccessItem {
        override val key = "playlist:$playlistId"
    }

    data class Track(
        val trackId: String,
        override val title: String,
        val artist: String,
        override val thumbnailUrl: String?,
    ) : QuickAccessItem {
        override val key = "track:$trackId"
    }
}
