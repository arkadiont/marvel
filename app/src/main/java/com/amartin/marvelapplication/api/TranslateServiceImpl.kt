package com.amartin.marvelapplication.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TranslateServiceImpl(baseUrl: String, key: String) {

    companion object {
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

        private fun client(apikey: String) = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authenticator(apikey))
            .build()
    }

    val service: TranslateService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client(key))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CallAdapterFactory())
        .build().create(TranslateService::class.java)
}