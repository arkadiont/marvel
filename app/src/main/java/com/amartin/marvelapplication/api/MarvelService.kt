package com.amartin.marvelapplication.api

import com.amartin.marvelapplication.api.model.CharacterDataWrapper
import com.amartin.marvelapplication.api.model.ComicDataWrapper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.codec.binary.Hex
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.security.MessageDigest

sealed class Result<out T> {
    class Success<T>(val data: T?): Result<T>()
    class Failure(val error: Int?): Result<Nothing>()
    object NetworkError: Result<Nothing>()
}

fun <T> Result<T>.onError(block: (String) -> Unit): Result<T> {
    when (this) {
        is Result.Failure -> if (error != null) block("error code: $error")
        is Result.NetworkError -> block("NetworkError")
    }
    return this
}

fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    when (this) {
        is Result.Success -> if (data != null) block(data)
    }
    return this
}

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

        abstract class CallDelegate<TIn, TOut>(protected val proxy: Call<TIn>): Call<TOut> {
            override fun execute(): retrofit2.Response<TOut> = throw NotImplementedError()
            final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
            final override fun clone(): Call<TOut> = cloneImpl()

            override fun cancel() = proxy.cancel()
            override fun request(): Request = proxy.request()
            override fun isCanceled(): Boolean = proxy.isCanceled
            override fun isExecuted(): Boolean = proxy.isExecuted

            abstract fun enqueueImpl(callback: Callback<TOut>)
            abstract fun cloneImpl(): Call<TOut>
        }

        private class ResultCall<T>(proxy: Call<T>): CallDelegate<T, Result<T>>(proxy) {
            override fun enqueueImpl(callback: Callback<Result<T>>) = proxy.enqueue(object:
                Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    val result = if (t is IOException) {
                        Result.NetworkError
                    }else {
                        Result.Failure(null)
                    }
                    callback.onResponse(this@ResultCall, retrofit2.Response.success(result))
                }

                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                    val code = response.code()
                    val result = if (code in 200 until 300) {
                        val body = response.body()
                        Result.Success(body)
                    }else {
                        Result.Failure(code)
                    }
                    callback.onResponse(this@ResultCall, retrofit2.Response.success(result))
                }
            })

            override fun cloneImpl(): Call<Result<T>> = ResultCall(proxy.clone())
        }

        private class ResultAdapter(private val type: Type): CallAdapter<Type, Call<Result<Type>>> {
            override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)
            override fun responseType(): Type = type
        }

        private class MarvelCallAdapterFactory: CallAdapter.Factory() {
            override fun get(
                returnType: Type,
                annotations: Array<Annotation>,
                retrofit: Retrofit
            )= when (getRawType(returnType)) {
                Call::class.java -> {
                    val callType = getParameterUpperBound(0, returnType as ParameterizedType)
                    when (getRawType(callType)) {
                        Result::class.java -> {
                            val resultType = getParameterUpperBound(0, callType as ParameterizedType)
                            ResultAdapter(resultType)
                        }
                        else -> null
                    }
                }
                else -> null
            }
        }

        fun create(privateKey: String, publicKey: String): MarvelService {
            val client = OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(authenticator(privateKey, publicKey))
                .build()
            return Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(MarvelCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MarvelService::class.java)
        }
    }
}