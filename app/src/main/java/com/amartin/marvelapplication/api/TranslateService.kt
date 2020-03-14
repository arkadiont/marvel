package com.amartin.marvelapplication.api

import com.amartin.marvelapplication.api.model.Translation
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslateService {

    @GET("/api/v1.5/tr.json/translate")
    suspend fun translate(@Query("text")text: String, @Query("lang")lang: String): Result<Translation>

    companion object {
        private const val baseUrl = "https://translate.yandex.net/"
        private const val key = "key"

        private val loggingInterceptor = HttpLoggingInterceptor().run {
            level = HttpLoggingInterceptor.Level.BASIC
            this
        }

        private fun authenticator(apikey: String) = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val newUrl = original.url.newBuilder().addQueryParameter(key, apikey).build()
                return chain.proceed(original.newBuilder().url(newUrl).build())
            }
        }

        fun create(apikey: String): TranslateService {
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authenticator(apikey))
                .build()
            return Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CallAdapterFactory())
                .build().create(TranslateService::class.java)
        }
    }

}