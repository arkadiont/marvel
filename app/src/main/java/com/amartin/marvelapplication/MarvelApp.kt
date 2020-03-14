package com.amartin.marvelapplication

import android.app.Application
import com.amartin.marvelapplication.di.DaggerMarvelComponent
import com.amartin.marvelapplication.di.MarvelComponent

class MarvelApp : Application() {

    lateinit var component: MarvelComponent
        private set

    override fun onCreate() {
        super.onCreate()

        component = DaggerMarvelComponent.factory().create(this)
    }
}