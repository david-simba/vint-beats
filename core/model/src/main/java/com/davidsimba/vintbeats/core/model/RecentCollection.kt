package com.davidsimba.vintbeats.core.model

import androidx.compose.runtime.Immutable

enum class RecentCollectionKind { PLAYLIST }

@Immutable
data class RecentCollection(
    val kind: RecentCollectionKind,
    val collectionId: String,
    val title: String,
    val thumbnailUrl: String?,
    val artistId: String?,
    val artistName: String?,
)
