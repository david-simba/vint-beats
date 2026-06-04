package com.davidsimba.vintbeats.feature.library.domain.track

import com.davidsimba.vintbeats.core.model.Track

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
