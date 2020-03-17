package com.amartin.marvelapplication.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

fun dispatcher(context: Context) = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: ""
        return when {
            path.startsWith("/v1/public/characters?orderBy=name&offset=0") ->
                MockResponse().fromJson(context, "characters_page1.json")

            path.startsWith("/v1/public/characters?orderBy=name&offset=20") ->
                MockResponse().fromJson(context, "characters_page2.json")

            path.startsWith("/v1/public/characters?orderBy=name&offset=40") ->
                MockResponse().fromJson(context, "characters_page3.json")

            path.startsWith("/v1/public/characters/1016823/comics") ->
                MockResponse().fromJson(context, "single_character_1016823.json")

            path.startsWith("/v1/public/characters/1016823") ->
                MockResponse().fromJson(context, "single_character_1016823.json")
            else -> MockResponse().setResponseCode(404)
        }
    }
}

private fun MockResponse.fromJson(context: Context, jsonFile: String): MockResponse =
    setBody(readJsonFile(context, jsonFile))

private fun readJsonFile(context: Context, jsonFilePath: String,
                         packageName: String = "com.amartin.marvelapplication.test"): String {
    val res: Resources = context.packageManager.getResourcesForApplication(packageName)

    var br: BufferedReader? = null

    try {
        br = BufferedReader(InputStreamReader(res.assets.open(jsonFilePath), UTF_8))
        var line: String?
        val text = StringBuilder()
        do {
            line = br.readLine()
            line?.let { text.append(line) }
        } while (line != null)
        br.close()
        return text.toString()
    } finally {
        br?.close()
    }
}

fun <T : Drawable> T.pixelsEqualTo(t: T?) = toBitmap().pixelsEqualTo(t?.toBitmap(), true)

fun Bitmap.pixelsEqualTo(otherBitmap: Bitmap?, shouldRecycle: Boolean = false) = otherBitmap?.let { other ->
    if (width == other.width && height == other.height) {
        val res = Arrays.equals(toPixels(), other.toPixels())
        if (shouldRecycle) {
            doRecycle().also { otherBitmap.doRecycle() }
        }
        res
    } else false
} ?: kotlin.run { false }

fun Bitmap.doRecycle() {
    if (!isRecycled) recycle()
}

fun <T : Drawable> T.toBitmap(): Bitmap {
    if (this is BitmapDrawable) return bitmap

    val drawable: Drawable = this
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun Bitmap.toPixels() = IntArray(width * height).apply { getPixels(this, 0, width, 0, 0, width, height) }