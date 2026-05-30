package com.davidsimba.vintbeats.core.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideSimpleCache(@ApplicationContext context: Context): SimpleCache {
        val cacheDir = File(context.cacheDir, "audio_cache")
        val evictor = LeastRecentlyUsedCacheEvictor(200L * 1024 * 1024) // 200 MB
        return SimpleCache(cacheDir, evictor, StandaloneDatabaseProvider(context))
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideCacheDataSourceFactory(cache: SimpleCache): CacheDataSource.Factory =
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory()
                    .setDefaultRequestProperties(
                        mapOf("User-Agent" to "com.google.android.youtube/21.03.36 (Linux; U; Android 11; en_US; Pixel 5 Build/RQ3A.210805.001) gzip")
                    )
            )
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
}
