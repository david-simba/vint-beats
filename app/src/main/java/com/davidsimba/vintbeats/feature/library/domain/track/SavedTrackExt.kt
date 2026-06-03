package com.davidsimba.vintbeats.feature.library.domain.track

fun SavedTrack.subtitle(): String =
    if (trackDurationText.isNotEmpty()) "$trackArtist • $trackDurationText"
    else trackArtist
