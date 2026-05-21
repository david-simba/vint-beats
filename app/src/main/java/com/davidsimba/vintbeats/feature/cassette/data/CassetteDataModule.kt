package com.davidsimba.vintbeats.feature.cassette.data

import com.davidsimba.vintbeats.feature.cassette.domain.CassetteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CassetteDataModule {

    @Binds
    @Singleton
    abstract fun bindCassetteRepository(impl: CassetteRepositoryImpl): CassetteRepository
}
