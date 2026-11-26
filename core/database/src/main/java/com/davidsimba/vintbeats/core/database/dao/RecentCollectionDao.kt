package com.davidsimba.vintbeats.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davidsimba.vintbeats.core.database.entity.RecentCollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentCollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentCollectionEntity)

    @Query("SELECT * FROM recent_collections WHERE kind = 'PLAYLIST' ORDER BY playedAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<RecentCollectionEntity>>
}
