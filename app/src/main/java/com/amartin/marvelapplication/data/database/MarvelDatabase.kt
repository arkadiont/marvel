package com.amartin.marvelapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Character::class], version = 1)
abstract class MarvelDatabase: RoomDatabase() {
    companion object {
        fun build(context: Context) = Room.databaseBuilder(context,
            MarvelDatabase::class.java, "marvel-db").build()
    }

    abstract fun marvelDao(): MarvelDao
}