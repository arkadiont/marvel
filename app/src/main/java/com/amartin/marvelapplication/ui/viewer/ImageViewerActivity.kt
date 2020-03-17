package com.amartin.marvelapplication.ui.viewer

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel.UiImageViewModel
import com.amartin.marvelapplication.ui.viewer.ImageViewerModel.UiImageViewModel.*
import kotlinx.android.synthetic.main.activity_viewer.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ImageViewerActivity : AppCompatActivity() {
    companion object {
        const val MAX_ZOOM_ALLOWED = 8.0f
        const val IMAGE_URL = "ImageViewer:image"
    }

    private val viewModel: ImageViewerModel by currentScope.viewModel(this) {
        parametersOf(intent.getStringExtra(IMAGE_URL))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_viewer)
        imageView.maxScale = MAX_ZOOM_ALLOWED
        viewModel.model.observe(this, Observer(::updateUi))
    }

    private fun updateUi(model: UiImageViewModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is Image -> imageView.setImage(model.image)
        }
    }
}