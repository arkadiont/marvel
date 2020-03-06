package com.amartin.marvelapplication.data.repository

import com.amartin.marvelapplication.data.model.CharacterData
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import com.amartin.marvelapplication.data.source.RemoteMarvelDataSource

class MarvelRepository(
    private val remoteMarvelDataSource: RemoteMarvelDataSource,
    private val localMarvelDataSource: LocalMarvelDataSource) {

    suspend fun getCharacters(offset: Int = 0) = remoteMarvelDataSource.getCharacters(offset)

    suspend fun getCharacter(id: Int) = remoteMarvelDataSource.getCharacter(id)

    suspend fun getComicsOfCharacter(id: Int) = remoteMarvelDataSource.getComicsOfCharacter(id)

    suspend fun saveFavouriteCharacter(character: CharacterData) = localMarvelDataSource.saveFavouriteCharacter(character)

    suspend fun getCountFavouriteCharacter(id: Int) = localMarvelDataSource.getCountFavouriteCharacter(id)

    suspend fun deleteFavouriteCharacter(character: CharacterData) = localMarvelDataSource.deleteFavouriteCharacter(character)
}