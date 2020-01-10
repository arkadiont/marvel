package com.amartin.marvelapplication.data.repository

import com.amartin.marvelapplication.data.source.RemoteMarvelDataSource

class MarvelRepository(private val remoteMarvelDataSource: RemoteMarvelDataSource) {

    suspend fun getCharacters(offset: Int = 0) = remoteMarvelDataSource.getCharacters(offset)

    suspend fun getCharacter(idCharacter: Int) = remoteMarvelDataSource.getCharacter(idCharacter)

    suspend fun getComicsOfCharacter(idCharacter: Int) = remoteMarvelDataSource.getComicsOfCharacter(idCharacter)

}