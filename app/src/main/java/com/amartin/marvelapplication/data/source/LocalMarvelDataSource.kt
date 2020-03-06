package com.amartin.marvelapplication.data.source

import com.amartin.marvelapplication.data.model.CharacterData

interface LocalMarvelDataSource {

    suspend fun getFavouriteCharacters(): List<CharacterData>

    suspend fun getFavouriteCharacter(id: Int): CharacterData

    suspend fun getCountFavouriteCharacter(id: Int): Int

    suspend fun saveFavouriteCharacter(characterData: CharacterData)

    suspend fun deleteFavouriteCharacter(characterData: CharacterData)
}