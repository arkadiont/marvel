package com.amartin.marvelapplication.data.database

import androidx.room.*

@Dao
interface CharacterWithComicsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveCharacterWithComics(characterComicRelation: CharacterComicRelation)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveCharacterWithComics(characterComicRelations: List<CharacterComicRelation>)

    @Transaction
    @Query("SELECT * FROM Character WHERE id = :characterId")
    fun getCharacterWithComics(characterId: Int):CharacterWithComics
}