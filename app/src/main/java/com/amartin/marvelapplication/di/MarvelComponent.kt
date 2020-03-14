package com.amartin.marvelapplication.di

import android.app.Application
import com.amartin.marvelapplication.ui.detail.DetailActivity
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailActivity
import com.amartin.marvelapplication.ui.favourite.FavouriteActivity
import com.amartin.marvelapplication.ui.main.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface MarvelComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(detailActivity: DetailActivity)
    fun inject(favouriteDetailActivity: FavouriteDetailActivity)
    fun inject(favouriteActivity: FavouriteActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): MarvelComponent
    }
}