package com.amartin.marvelapplication.api

import com.amartin.marvelapplication.api.model.Translation
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslateService {

    @GET("/api/v1.5/tr.json/translate")
    suspend fun translate(@Query("text")text: String, @Query("lang")lang: String): Result<Translation>

}