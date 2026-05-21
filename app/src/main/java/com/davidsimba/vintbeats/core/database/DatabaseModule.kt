package com.davidsimba.vintbeats.core.database

import android.content.Context
import androidx.room.Room
import com.davidsimba.vintbeats.feature.cassette.data.CassetteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VintBeatsDatabase =
        Room.databaseBuilder(context, VintBeatsDatabase::class.java, "vintbeats.db")
            .addMigrations(VintBeatsDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideCassetteDao(db: VintBeatsDatabase): CassetteDao = db.cassetteDao()
}
