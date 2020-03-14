package com.amartin.marvelapplication.di

import android.app.Application
import com.amartin.marvelapplication.api.MarvelService
import com.amartin.marvelapplication.api.TranslateService
import com.amartin.marvelapplication.common.Credentials
import com.amartin.marvelapplication.data.database.MarvelDatabase
import com.amartin.marvelapplication.data.database.RoomDataSource
import com.amartin.marvelapplication.data.impl.MarvelCharacterRemoteMarvelDataSource
import com.amartin.marvelapplication.data.impl.PermissionCheckerImpl
import com.amartin.marvelapplication.data.impl.PlayServicesLocationDataSource
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.data.repository.RegionRepository
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import com.amartin.marvelapplication.data.source.LocationDataSource
import com.amartin.marvelapplication.data.source.PermissionChecker
import com.amartin.marvelapplication.data.source.RemoteMarvelDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun databaseProvider(app: Application): MarvelDatabase = MarvelDatabase.build(app)

    @Provides
    fun locationDataSourceProvider(app: Application): LocationDataSource =
        PlayServicesLocationDataSource(app)

    @Provides
    fun permissionCheckerProvider(app: Application): PermissionChecker =
        PermissionCheckerImpl(app)

    @Provides
    fun remoteMarvelDataSourceProvider(): RemoteMarvelDataSource =
        MarvelCharacterRemoteMarvelDataSource(
            MarvelService.create(Credentials.privateKey, Credentials.publicKey))

    @Provides
    fun localMarvelDataSourceProvider(db: MarvelDatabase): LocalMarvelDataSource =
        RoomDataSource(db)

    @Provides
    fun marvelRepositoryProvider(
        remoteDataSource: RemoteMarvelDataSource,
        localDataSource: LocalMarvelDataSource) = MarvelRepository(remoteDataSource, localDataSource)

    @Provides
    fun regionRepositoryProvider(
        locationDataSource: LocationDataSource,
        permissionChecker: PermissionChecker) = RegionRepository(locationDataSource, permissionChecker)

    @Provides
    fun translateServiceProvider(): TranslateService = TranslateService.create(Credentials.yandexApikey)
}