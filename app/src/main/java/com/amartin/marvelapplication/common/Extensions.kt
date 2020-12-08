package com.amartin.marvelapplication.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.amartin.marvelapplication.MarvelApp
import com.amartin.marvelapplication.data.model.Thumbnail
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = true): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun ImageView.loadUrl(url: String) =
    Glide.with(context).load(url).placeholder(CircularProgressDrawable(context).apply { start() }).into(this)

fun TextView.htmlLink(source: String) {
    text = HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY)
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.html(source: String) {
    text = HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Thumbnail.getUrl() = "${path.replaceFirst("http://", "https://")}.${extension}"

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG) =
    Toast.makeText(this, message, duration).show()

fun View.snackBar(message: String, duration: Int = Snackbar.LENGTH_LONG) =
    Snackbar.make(this, message, duration).show()

fun AppCompatActivity.snackBar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    findViewById<View>(android.R.id.content).snackBar(message, duration)
}

inline fun <reified T : Activity> Context.intentFor(body: Intent.() -> Unit): Intent =
    Intent(this, T::class.java).apply(body)

inline fun <reified T : Activity> Context.startActivity(body: Intent.() -> Unit) {
    startActivity(intentFor<T>(body))
}

@Suppress("UNCHECKED_CAST")
class BaseViewModelFactory<T>(val creator: () -> T): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = creator() as T
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

val Context.app: MarvelApp
    get() = applicationContext as MarvelApp