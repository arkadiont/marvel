package com.amartin.marvelapplication.data.impl

import com.amartin.marvelapplication.api.MarvelService
import com.amartin.marvelapplication.api.Result
import com.amartin.marvelapplication.api.model.CharacterDataWrapper
import com.amartin.marvelapplication.api.model.ComicDataWrapper
import com.amartin.marvelapplication.data.source.RemoteMarvelDataSource

class MarvelCharacterRemoteMarvelDataSource(private val marvelService: MarvelService) : RemoteMarvelDataSource {

    override suspend fun getCharacters(offset: Int): Result<CharacterDataWrapper> =
        marvelService.getCharacters(offset)

    override suspend fun getCharacter(idCharacter: Int): Result<CharacterDataWrapper> =
        marvelService.getCharacter(idCharacter)

    override suspend fun getComicsOfCharacter(idCharacter: Int): Result<ComicDataWrapper> =
        marvelService.getComicsOfCharacter(idCharacter)


}