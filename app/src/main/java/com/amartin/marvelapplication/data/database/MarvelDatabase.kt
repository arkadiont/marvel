package com.amartin.marvelapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Character::class, Comic::class, CharacterComicRelation::class, Url::class], version = 1, exportSchema = false)
abstract class MarvelDatabase: RoomDatabase() {
    companion object {
        fun build(context: Context) = Room.databaseBuilder(context,
            MarvelDatabase::class.java, "marvel.db").build()
    }

    abstract fun characterDao(): CharacterDao

    abstract fun comicDao(): ComicDao

    abstract fun urlDao(): UrlDao

    abstract fun characterWithComicsDao(): CharacterWithComicsDao
}