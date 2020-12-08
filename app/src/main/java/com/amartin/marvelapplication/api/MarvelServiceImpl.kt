package com.amartin.marvelapplication.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

class MarvelServiceImpl(baseUrl: String, privateKey: String, publicKey: String) {
    companion object {
        private val DIGITS_LOWER = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'
        )
        private val DIGITS_UPPER = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'
        )
        private const val TS = "ts"
        private const val HASH = "hash"
        private const val APIKEY = "apikey"
        private const val MD5 = "MD5"

        private fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
            val l = data.size
            val out = CharArray(l shl 1)
            // two characters form the hex value.
            var i = 0
            var j = 0
            while (i < l) {
                out[j++] = toDigits[0xF0 and data[i].toInt() ushr 4]
                out[j++] = toDigits[0x0F and data[i].toInt()]
                i++
            }
            return out
        }

        private fun encodeHexString(data: ByteArray, toLowerCase: Boolean = true): String {
            val out = if (toLowerCase) encodeHex(data, DIGITS_LOWER) else encodeHex(data, DIGITS_UPPER)
            return String(out)
        }

        private fun md5(message: String): String {
            val instance = MessageDigest.getInstance(MD5)
            return encodeHexString(instance.digest(message.toByteArray()))
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

        private fun client(privateKey: String, publicKey: String) = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(authenticator(privateKey, publicKey))
            .build()
    }

    val okHttpClient = client(privateKey, publicKey)

    val service: MarvelService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(CallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(MarvelService::class.java)

}