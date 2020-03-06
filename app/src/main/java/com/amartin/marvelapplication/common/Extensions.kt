package com.amartin.marvelapplication.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.amartin.marvelapplication.MarvelApp
import com.amartin.marvelapplication.data.model.Thumbnail
import com.amartin.marvelapplication.data.model.Url
import com.bumptech.glide.Glide

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = true): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun ImageView.loadUrl(url: String) =
    Glide.with(context).load(url).placeholder(CircularProgressDrawable(context).apply { start() }).into(this)

fun TextView.loadAnimation(idAnim: Int) {
    startAnimation(AnimationUtils.loadAnimation(context, idAnim))
}

fun Thumbnail.getUrl() = "${path.replaceFirst("http://", "https://")}.${extension}"

fun Url.getUrlHtml() = "<a href=\"${url}\">${type}</a>"

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, message, duration).show()

inline fun <reified T : Activity> Context.intentFor(body: Intent.() -> Unit): Intent =
    Intent(this, T::class.java).apply(body)

inline fun <reified T : Activity> Context.startActivity(body: Intent.() -> Unit) {
    startActivity(intentFor<T>(body))
}

val Context.app: MarvelApp
    get() = applicationContext as MarvelApp