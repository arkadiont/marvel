package com.amartin.marvelapplication.api

import com.amartin.marvelapplication.api.model.CharacterDataWrapper
import com.amartin.marvelapplication.api.model.ComicDataWrapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.codec.binary.Hex
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.security.MessageDigest

interface MarvelService {

    @GET("/v1/public/characters?orderBy=name")
    suspend fun getCharacters(@Query("offset")offset: Int = 0): Result<CharacterDataWrapper>

    @GET("/v1/public/characters/{id}")
    suspend fun getCharacter(@Path("id")id: Int): Result<CharacterDataWrapper>

    @GET("/v1/public/characters/{id}/comics")
    suspend fun getComicsOfCharacter(@Path("id")id: Int): Result<ComicDataWrapper>

    companion object {
        private const val BASE_URL = "https://gateway.marvel.com:443/"
        private const val TS = "ts"
        private const val HASH = "hash"
        private const val APIKEY = "apikey"

        private const val MD5 = "MD5"

        private fun md5(message: String): String {
            val instance = MessageDigest.getInstance(MD5)
            return Hex.encodeHexString(instance.digest(message.toByteArray()))
        }

        private fun hash(ts: Long, privateKey: String, publicKey: String): String =
            md5("$ts$privateKey$publicKey")

        private val logInterceptor = HttpLoggingInterceptor().run {
            level = HttpLoggingInterceptor.Level.BASIC
            this
        }

        private fun authenticator(privateKey: String, publicKey: String) = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val ts = System.currentTimeMillis()
                val originalRequest = chain.request()
                val newUrl = originalRequest.url.newBuilder()
                    .addQueryParameter(TS, ts.toString())
                    .addQueryParameter(APIKEY, publicKey)
                    .addQueryParameter(HASH, hash(ts, privateKey, publicKey))
                    .build()

                return chain.proceed(originalRequest.newBuilder().url(newUrl).build())
            }
        }

        fun create(privateKey: String, publicKey: String): MarvelService {
            val client = OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(authenticator(privateKey, publicKey))
                .build()
            return Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(CallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MarvelService::class.java)
        }
    }
}