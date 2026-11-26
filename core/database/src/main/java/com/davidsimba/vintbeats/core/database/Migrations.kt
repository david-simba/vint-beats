package com.davidsimba.vintbeats.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Añade el historial de playlists y radios recientes sin tocar el resto de tablas. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `recent_collections` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`kind` TEXT NOT NULL, " +
                "`collectionId` TEXT NOT NULL, " +
                "`title` TEXT NOT NULL, " +
                "`thumbnailUrl` TEXT, " +
                "`artistId` TEXT, " +
                "`artistName` TEXT, " +
                "`playedAt` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_recent_collections_kind_collectionId` " +
                "ON `recent_collections` (`kind`, `collectionId`)"
        )
    }
}
