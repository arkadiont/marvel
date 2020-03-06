package com.amartin.marvelapplication.ui.viewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amartin.marvelapplication.common.ViewModelScope
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel.UiImageViewModel.*
import com.davemorrissey.labs.subscaleview.ImageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
class ImageViewerModel(private val imageUrl: String) : ViewModelScope() {

    sealed class UiImageViewModel {
        object Loading : UiImageViewModel()
        class Image(val image: ImageSource) : UiImageViewModel()
    }

    private val _model = MutableLiveData<UiImageViewModel>()
    val model: LiveData<UiImageViewModel>
        get() {
            if (_model.value == null) refresh()
            return _model
        }

    private fun refresh() {
        launch {
            _model.value = Loading
            _model.value = Image(ImageSource.bitmap(decodeStreamAsBitmap()))
        }
    }

    private suspend fun decodeStreamAsBitmap(): Bitmap =
        withContext(Dispatchers.IO) {
            BitmapFactory.decodeStream(URL(imageUrl).openStream())
        }
}


@Suppress("UNCHECKED_CAST")
class ImageViewerModelFactory(private val imageUrl: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ImageViewerModel(imageUrl) as T
}