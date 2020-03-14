package com.amartin.marvelapplication.api

import com.amartin.marvelapplication.api.model.CharacterDataWrapper
import com.amartin.marvelapplication.api.model.ComicDataWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelService {

    @GET("/v1/public/characters?orderBy=name")
    suspend fun getCharacters(@Query("offset")offset: Int = 0): Result<CharacterDataWrapper>

    @GET("/v1/public/characters/{id}")
    suspend fun getCharacter(@Path("id")id: Int): Result<CharacterDataWrapper>

    @GET("/v1/public/characters/{id}/comics")
    suspend fun getComicsOfCharacter(@Path("id")id: Int): Result<ComicDataWrapper>

}