package com.amartin.marvelapplication.api

import okhttp3.Request
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

data class ErrorCode(val code: Int)

sealed class Result<out T> {
    class Success<T>(val data: T?): Result<T>()
    class Failure(val error: String?): Result<Nothing>()
    object NetworkError: Result<Nothing>()
}

fun <T> Result<T>.onError(block: (String) -> Unit): Result<T> {
    when (this) {
        is Result.Failure -> if (error != null) block(error)
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
    override fun enqueueImpl(callback: Callback<Result<T>>) = proxy.enqueue(object: Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = if (t is IOException) {
                t.printStackTrace()
                Result.NetworkError
            }else {
                Result.Failure(null)
            }
            callback.onResponse(this@ResultCall, retrofit2.Response.success(result))
        }

        override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val success = Result.Success(response.body())
                success
            }else {
                Result.Failure(response.errorBody()?.string() ?: ErrorCode(code).toString())
            }
            callback.onResponse(this@ResultCall, retrofit2.Response.success(result))
        }
    })

    override fun cloneImpl(): Call<Result<T>> = ResultCall(proxy.clone())
}

class ResultAdapter(private val type: Type): CallAdapter<Type, Call<Result<Type>>> {
    override fun adapt(call: Call<Type>): Call<Result<Type>> = ResultCall(call)
    override fun responseType(): Type = type
}

class CallAdapterFactory: CallAdapter.Factory() {
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