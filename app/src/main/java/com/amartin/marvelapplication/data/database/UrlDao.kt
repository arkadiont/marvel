package com.amartin.marvelapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UrlDao {
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveUrl(urls: List<Url>)

    @Query("SELECT * FROM Url WHERE characterId = :id")
    fun findByCharacterId(id: Int):List<Url>

    @Query("DELETE FROM Url WHERE characterId = :characterId")
    fun deleteUrlFrom(characterId: Int)
}