package com.davidsimba.vintbeats.core.model


fun SavedTrack.subtitle(): String =
    if (trackDurationText.isNotEmpty()) "$trackArtist • $trackDurationText"
    else trackArtist

fun SavedTrack.toTrack(): Track = Track(
    id = trackId,
    title = trackTitle,
    artist = trackArtist,
    albumImageUrl = trackThumbnailUrl,
    previewUrl = null,
    durationText = trackDurationText
)
