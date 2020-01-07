package com.amartin.marvelapplication.data.repository

import com.amartin.marvelapplication.data.source.RemoteDataSource

class MarvelRepository(private val remoteDataSource: RemoteDataSource) {

    suspend fun getCharacters(offset: Int = 0) = remoteDataSource.getCharacters(offset)

    suspend fun getCharacter(idCharacter: Int) = remoteDataSource.getCharacter(idCharacter)

    suspend fun getComicsOfCharacter(idCharacter: Int) = remoteDataSource.getComicsOfCharacter(idCharacter)

}