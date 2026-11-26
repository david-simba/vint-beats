package com.davidsimba.vintbeats.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davidsimba.vintbeats.core.model.RecentCollection
import com.davidsimba.vintbeats.core.model.RecentCollectionKind

@Entity(
    tableName = "recent_collections",
    indices = [Index(value = ["kind", "collectionId"], unique = true)]
)
data class RecentCollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kind: String,
    val collectionId: String,
    val title: String,
    val thumbnailUrl: String?,
    val artistId: String?,
    val artistName: String?,
    val playedAt: Long = System.currentTimeMillis()
) {
    /** Devuelve null si el tipo guardado ya no existe (filas de versiones anteriores). */
    fun toDomain(): RecentCollection? {
        val kind = RecentCollectionKind.entries.firstOrNull { it.name == kind } ?: return null
        return RecentCollection(
            kind = kind,
            collectionId = collectionId,
            title = title,
            thumbnailUrl = thumbnailUrl,
            artistId = artistId,
            artistName = artistName,
        )
    }
}
