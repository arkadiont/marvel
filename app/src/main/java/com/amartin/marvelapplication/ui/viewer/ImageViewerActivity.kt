package com.amartin.marvelapplication.ui.viewer

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.getViewModel
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel.UiImageViewModel
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel.UiImageViewModel.*
import kotlinx.android.synthetic.main.activity_viewer.*
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalStateException

class ImageViewerActivity : AppCompatActivity() {
    companion object {
        const val MAX_ZOOM_ALLOWED = 8.0f
        const val IMAGE_URL = "ImageViewer:image"
    }

    private lateinit var imageUrl: String
    private val viewModel: ImageViewerModel by lazy {
        getViewModel { ImageViewerModel(imageUrl, Dispatchers.Main) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_viewer)

        imageView.maxScale = MAX_ZOOM_ALLOWED
        imageUrl = intent.getStringExtra(IMAGE_URL)?.toString() ?: ""
        if (imageUrl.isBlank()) throw IllegalStateException("ImageUrl not found")

        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: UiImageViewModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is Image -> imageView.setImage(model.image)
        }
    }
}