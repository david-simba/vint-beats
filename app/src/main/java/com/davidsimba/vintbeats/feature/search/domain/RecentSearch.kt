package com.davidsimba.vintbeats.feature.search.domain

import com.davidsimba.vintbeats.core.model.Album
import com.davidsimba.vintbeats.core.model.Artist
import com.davidsimba.vintbeats.core.model.Track

data class RecentSearch(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val thumbnailUrl: String? = null,
    val type: Type,
) {
    enum class Type { TRACK, ALBUM, ARTIST }

    fun toTrack() = Track(
        id = id, title = title, artist = subtitle ?: "",
        albumImageUrl = thumbnailUrl, previewUrl = null, durationText = ""
    )

    fun toAlbum() = Album(id = id, title = title, thumbnailUrl = thumbnailUrl, year = null)

    fun toArtist() = Artist(id = id, name = title, thumbnailUrl = thumbnailUrl, subtitle = subtitle)
}
