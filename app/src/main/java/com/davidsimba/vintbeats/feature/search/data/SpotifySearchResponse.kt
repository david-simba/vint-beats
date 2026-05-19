package com.davidsimba.vintbeats.feature.search.data

import com.google.gson.annotations.SerializedName

data class SpotifySearchResponse(
    val tracks: TracksPaging
)

data class TracksPaging(
    val items: List<TrackDto>
)

data class TrackDto(
    val id: String,
    val name: String,
    val artists: List<ArtistDto>,
    val album: AlbumDto,
    @SerializedName("preview_url") val previewUrl: String?
)

data class ArtistDto(
    val name: String
)

data class AlbumDto(
    val images: List<AlbumImageDto>
)

data class AlbumImageDto(
    val url: String
)