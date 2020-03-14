package com.amartin.marvelapplication.di

import android.app.Application
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.api.MarvelServiceImpl
import com.amartin.marvelapplication.api.TranslateService
import com.amartin.marvelapplication.api.TranslateServiceImpl
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
import javax.inject.Named
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
    @Singleton
    @Named("baseUrlMarvel")
    fun marvelBaseUrlProvider(app: Application) = app.getString(R.string.base_url_marvel)

    @Provides
    @Singleton
    @Named("marvelPublicKey")
    fun marvelPublicKeyProvider(app: Application) = app.getString(R.string.marvel_public_key)

    @Provides
    @Singleton
    @Named("marvelPrivateKey")
    fun marvelPrivateKeyProvider(app: Application) = app.getString(R.string.marvel_private_key)

    @Provides
    fun remoteMarvelDataSourceProvider(
        @Named("baseUrlMarvel") baseUrlMarvel: String,
        @Named("marvelPrivateKey") marvelPrivateKey: String,
        @Named("marvelPublicKey") marvelPublicKey: String): RemoteMarvelDataSource =
            MarvelCharacterRemoteMarvelDataSource(
                MarvelServiceImpl(baseUrlMarvel, marvelPrivateKey, marvelPublicKey).service)

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
    @Singleton
    @Named("baseUrlYandex")
    fun yandexBaseUrl(app: Application) = app.getString(R.string.base_url_yandex)

    @Provides
    @Singleton
    @Named("yandexKey")
    fun yandexKeyProvider(app: Application) = app.getString(R.string.yandex_key)

    @Provides
    fun translateServiceProvider(@Named("baseUrlYandex") baseUrlYandex: String,
                                 @Named("yandexKey") yandexKey: String): TranslateService =
        TranslateServiceImpl(baseUrlYandex, yandexKey).service
}