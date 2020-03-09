package com.amartin.marvelapplication.data.database

import com.amartin.marvelapplication.data.mappers.toCharacter
import com.amartin.marvelapplication.data.mappers.toCharacterComicData
import com.amartin.marvelapplication.data.mappers.toCharacterData
import com.amartin.marvelapplication.data.mappers.toComic
import com.amartin.marvelapplication.data.model.CharacterComicData
import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.model.ComicData
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomDataSource(db: MarvelDatabase): LocalMarvelDataSource {

    private val characterDao = db.characterDao()
    private val comicDao = db.comicDao()
    private val characterWithComicsDao = db.characterWithComicsDao()

    override suspend fun getFavouriteCharacters(): List<CharacterData> = withContext(Dispatchers.IO) {
        characterDao.getAll().map { it.toCharacterData() }
    }

    override suspend fun getFavouriteCharacter(id: Int): CharacterData = withContext(Dispatchers.IO) {
        characterDao.findById(id).toCharacterData()
    }

    override suspend fun getFavouriteCharacterWithComics(id: Int): CharacterComicData = withContext(Dispatchers.IO) {
        characterWithComicsDao.getCharacterWithComics(id).toCharacterComicData()
    }

    override suspend fun getCountFavouriteCharacter(id: Int): Int = withContext(Dispatchers.IO) {
        characterDao.countById(id)
    }

    override suspend fun saveFavouriteCharacter(characterData: CharacterData, comicData: List<ComicData>) = withContext(Dispatchers.IO) {
        comicDao.saveComic(comicData.map { it.toComic() })
        characterDao.saveCharacter(characterData.toCharacter())
        characterWithComicsDao.saveCharacterWithComics(comicData.map { CharacterComicRelation(characterData.id, it.id) })
    }

    override suspend fun deleteFavouriteCharacter(characterData: CharacterData) = withContext(Dispatchers.IO) {
        characterDao.deleteCharacter(characterData.toCharacter())
    }


}