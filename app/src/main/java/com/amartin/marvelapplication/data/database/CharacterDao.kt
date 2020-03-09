package com.amartin.marvelapplication.data.database

import androidx.room.*

@Dao
interface CharacterDao {
    @Query("SELECT * FROM Character")
    fun getAll(): List<Character>

    @Query("SELECT * FROM Character WHERE id = :id" )
    fun findById(id: Int): Character

    @Query("SELECT count(*) FROM Character WHERE id = :id" )
    fun countById(id: Int): Int

    @Delete(entity = Character::class)
    fun deleteCharacter(character: Character)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveCharacter(character: Character)
}