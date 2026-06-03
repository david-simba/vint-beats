package com.davidsimba.vintbeats.feature.library.data.playlist

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davidsimba.vintbeats.feature.library.data.track.SavedTrackEntity

data class PlaylistWithTracksEntity(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistTrackCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "savedTrackId",
        ),
    )
    val tracks: List<SavedTrackEntity>,
)
