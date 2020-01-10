package com.amartin.marvelapplication.data.source

import com.amartin.marvelapplication.api.Result
import com.amartin.marvelapplication.api.model.CharacterDataWrapper
import com.amartin.marvelapplication.api.model.ComicDataWrapper

interface RemoteMarvelDataSource {

    suspend fun getCharacters(offset: Int): Result<CharacterDataWrapper>

    suspend fun getCharacter(idCharacter: Int): Result<CharacterDataWrapper>

    suspend fun getComicsOfCharacter(idCharacter: Int): Result<ComicDataWrapper>

}

