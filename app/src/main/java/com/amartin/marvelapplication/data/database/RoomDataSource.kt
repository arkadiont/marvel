package com.amartin.marvelapplication.data.database

import com.amartin.marvelapplication.data.mappers.toCharacter
import com.amartin.marvelapplication.data.mappers.toCharacterData
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomDataSource(db: MarvelDatabase): LocalMarvelDataSource {

    private val marvelDao = db.marvelDao()

    override suspend fun getFavouriteCharacters(): List<CharacterData> = withContext(Dispatchers.IO) {
        marvelDao.getAll().map { it.toCharacterData() }
    }

    override suspend fun getFavouriteCharacter(id: Int): CharacterData = withContext(Dispatchers.IO) {
        marvelDao.findById(id).toCharacterData()
    }

    override suspend fun getCountFavouriteCharacter(id: Int): Int = withContext(Dispatchers.IO) {
        marvelDao.countById(id)
    }

    override suspend fun saveFavouriteCharacter(characterData: CharacterData) = withContext(Dispatchers.IO) {
        marvelDao.saveCharacter(characterData.toCharacter())
    }

    override suspend fun deleteFavouriteCharacter(characterData: CharacterData) = withContext(Dispatchers.IO) {
        marvelDao.deleteCharacter(characterData.toCharacter())
    }


}