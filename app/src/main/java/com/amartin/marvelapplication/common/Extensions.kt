package com.amartin.marvelapplication.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.text.HtmlCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.amartin.marvelapplication.MarvelApp
import com.amartin.marvelapplication.data.model.Thumbnail
import com.amartin.marvelapplication.data.model.Url
import com.bumptech.glide.Glide

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = true): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun ImageView.loadUrl(url: String) =
    Glide.with(context).load(url).placeholder(CircularProgressDrawable(context).apply { start() }).into(this)

fun TextView.htmlLink(source: String) {
    text = HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY)
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.loadAnimation(idAnim: Int) {
    startAnimation(AnimationUtils.loadAnimation(context, idAnim))
}

fun Thumbnail.getUrl() = "${path.replaceFirst("http://", "https://")}.${extension}"

fun Url.getUrlHtml() = "<a href=\"${url}\">${type}</a>"

fun html(htmlText: String): Spanned = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    else -> Html.fromHtml(htmlText)
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, message, duration).show()

inline fun <reified T : Activity> Context.intentFor(body: Intent.() -> Unit): Intent =
    Intent(this, T::class.java).apply(body)

inline fun <reified T : Activity> Context.startActivity(body: Intent.() -> Unit) {
    startActivity(intentFor<T>(body))
}

val Context.app: MarvelApp
    get() = applicationContext as MarvelApp