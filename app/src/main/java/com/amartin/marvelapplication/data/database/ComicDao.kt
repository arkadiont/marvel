package com.amartin.marvelapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ComicDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveComic(comics: List<Comic>)
}