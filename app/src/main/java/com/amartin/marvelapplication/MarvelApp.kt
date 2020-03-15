package com.amartin.marvelapplication

import android.app.Application
import com.amartin.marvelapplication.di.initDI

class MarvelApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initDI()
    }
}