package com.amartin.marvelapplication.ui.favorite_detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.common.adapter.ComicAdapter
import com.amartin.marvelapplication.data.source.LocalMarvelDataSource
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.Content
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.Loading
import com.amartin.marvelapplication.ui.viewer.ImageViewerActivity
import kotlinx.android.synthetic.main.activity_favourite_detail.*
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class FavouriteDetailActivity: AppCompatActivity() {

    companion object {
        const val CHARACTER = "FavouriteDetailActivity:character"
    }

    private var characterId: Int = -1
    private val viewModel by lazy {
        getViewModel { FavouriteDetailViewModel(localMarvelDataSource, characterId, Dispatchers.Main) }
    }
    private lateinit var comicAdapter: ComicAdapter

    @Inject lateinit var localMarvelDataSource: LocalMarvelDataSource

    private fun initAdapters() {
        comicAdapter = ComicAdapter(viewModel::onComicImageClick)
        comicRecyclerView.setHasFixedSize(true)
        comicRecyclerView.adapter = comicAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_detail)

        characterId = intent.getIntExtra(CHARACTER, -1)
        if (characterId == -1) throw IllegalStateException("Character not found")

        app.component.inject(this)

        initAdapters()

        viewModel.model.observe(this, Observer(::updateUi))
        viewModel.navigate.observe(this, Observer(::navigate))
    }

    private fun navigate(eventUrl: Event<String>) {
        eventUrl.getContentIfNotHandled()?.let {
            startActivity<ImageViewerActivity> {
                putExtra(ImageViewerActivity.IMAGE_URL, it)
            }
        }
    }

    private fun updateUi(model: FavouriteDetailViewModel.UiModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is Content -> {
                with(model.characterComicData.characterData) {
                    characterDetailToolbar.title = name
                    characterDetailImage.loadUrl(thumbnail.getUrl())
                    characterDetailImage.setOnClickListener{ viewModel.onCharacterClick(this) }
                    characterDescription.text =
                        if (description.isNotBlank()) description else getString(R.string.not_available)
                }
                comicAdapter.updateComicList(model.characterComicData.comicData)
            }
        }
    }
}