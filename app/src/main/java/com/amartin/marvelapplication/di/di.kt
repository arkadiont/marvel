package com.amartin.marvelapplication.di

import android.app.Application
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.api.MarvelServiceImpl
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
import com.amartin.marvelapplication.di.NamesDI.baseUrlMarvel
import com.amartin.marvelapplication.di.NamesDI.baseUrlYandex
import com.amartin.marvelapplication.di.NamesDI.marvelPrivateKey
import com.amartin.marvelapplication.di.NamesDI.marvelPublicKey
import com.amartin.marvelapplication.di.NamesDI.yandexKey
import com.amartin.marvelapplication.ui.detail.DetailActivity
import com.amartin.marvelapplication.ui.detail.DetailViewModel
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailActivity
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel
import com.amartin.marvelapplication.ui.favourite.FavouriteActivity
import com.amartin.marvelapplication.ui.favourite.FavouriteViewModel
import com.amartin.marvelapplication.ui.main.MainActivity
import com.amartin.marvelapplication.ui.main.MainViewModel
import com.amartin.marvelapplication.ui.viewer.ImageViewerActivity
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NamesDI {
    const val baseUrlMarvel = "baseUrlMarvel"
    const val baseUrlYandex = "baseUrlYandex"
    const val marvelPublicKey = "marvelPublicKey"
    const val marvelPrivateKey = "marvelPrivateKey"
    const val yandexKey = "yandexKey"
}

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(listOf(appModule, androidModule))
    }
}

private val appModule = module {
    single(named(baseUrlMarvel)) { androidApplication().getString(R.string.base_url_marvel) }
    single(named(marvelPublicKey)) { androidApplication().getString(R.string.marvel_public_key) }
    single(named(marvelPrivateKey)) { androidApplication().getString(R.string.marvel_private_key) }
    single {
        MarvelServiceImpl(
            get(named(baseUrlMarvel)),
            get(named(marvelPrivateKey)),
            get(named(marvelPublicKey)))
    }
    single<RemoteMarvelDataSource> {
        MarvelCharacterRemoteMarvelDataSource( get<MarvelServiceImpl>().service )
    }

    single(named(baseUrlYandex)) { androidApplication().getString(R.string.base_url_yandex) }
    single(named(yandexKey)) { androidApplication().getString(R.string.yandex_key) }
    single {
        TranslateServiceImpl(
            get(named(baseUrlYandex)),
            get(named(yandexKey))).service
    }

    single { MarvelDatabase.build(get()) }
    single<CoroutineDispatcher> { Dispatchers.Main }

    factory<LocationDataSource> { PlayServicesLocationDataSource(get()) }
    factory<PermissionChecker> { PermissionCheckerImpl(get()) }
    factory<LocalMarvelDataSource> { RoomDataSource(get()) }
    factory { MarvelRepository(get(), get()) }
    factory { RegionRepository(get(), get()) }
}

val androidModule = module {
    scope(named<MainActivity>()) {
        viewModel { MainViewModel(get(), get()) }
    }
    scope(named<DetailActivity>()) {
        viewModel { (id: Int) -> DetailViewModel(get(), get(), get(), id, get()) }
    }
    scope(named<FavouriteActivity>()) {
        viewModel { FavouriteViewModel(get(), get()) }
    }
    scope(named<FavouriteDetailActivity>()) {
        viewModel { (id: Int) -> FavouriteDetailViewModel(get(), id, get()) }
    }
    scope(named<ImageViewerActivity>()) {
        viewModel { (id: String) -> ImageViewerModel(id, get()) }
    }
}