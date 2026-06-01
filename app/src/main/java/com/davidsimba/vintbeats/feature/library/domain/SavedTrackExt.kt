package com.davidsimba.vintbeats.feature.library.domain

fun SavedTrack.subtitle(): String =
    if (trackDurationText.isNotEmpty()) "$trackArtist • $trackDurationText"
    else trackArtist
