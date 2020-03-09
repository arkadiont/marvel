package com.amartin.marvelapplication.data.source

import com.amartin.marvelapplication.data.model.CharacterComicData
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.ComicData

interface LocalMarvelDataSource {

    suspend fun getFavouriteCharacters(): List<CharacterData>

    suspend fun getFavouriteCharacter(id: Int): CharacterData

    suspend fun getFavouriteCharacterWithComics(id: Int): CharacterComicData

    suspend fun getCountFavouriteCharacter(id: Int): Int

    suspend fun saveFavouriteCharacter(characterData: CharacterData, comicData: List<ComicData> = emptyList())

    suspend fun deleteFavouriteCharacter(characterData: CharacterData)
}