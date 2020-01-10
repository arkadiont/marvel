package com.amartin.marvelapplication.ui.viewer

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel.UiImageViewModel
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel.UiImageViewModel.*
import kotlinx.android.synthetic.main.activity_viewer.*

class ImageViewer : AppCompatActivity() {
    companion object {
        const val MAX_ZOOM_ALLOWED = 8.0f
        const val IMAGE_URL = "ImageViewer:image"
    }

    private lateinit var viewModel: ImageViewerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_viewer)

        imageView.maxScale = MAX_ZOOM_ALLOWED
        val imageUrl = intent.getStringExtra(IMAGE_URL)
        if (imageUrl == null || imageUrl.isBlank()) throw IllegalStateException("ImageUrl not found")

        viewModel = ViewModelProviders.of(this,
            ImageViewerModelFactory(imageUrl))[ImageViewerModel::class.java]

        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: UiImageViewModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is Image -> imageView.setImage(model.image)
        }
    }
}