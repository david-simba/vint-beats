package com.davidsimba.vintbeats.di

import com.davidsimba.vintbeats.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/** Configuración que solo conoce :app (BuildConfig) y que los módulos core necesitan. */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    @Named("backendUrl")
    fun provideBackendUrl(): String = BuildConfig.BACKEND_URL
}
