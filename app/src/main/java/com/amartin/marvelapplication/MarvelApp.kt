package com.amartin.marvelapplication

import android.app.Application
import com.amartin.marvelapplication.data.database.MarvelDatabase

class MarvelApp : Application() {

    lateinit var db: MarvelDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        db = MarvelDatabase.build(this)
    }
}