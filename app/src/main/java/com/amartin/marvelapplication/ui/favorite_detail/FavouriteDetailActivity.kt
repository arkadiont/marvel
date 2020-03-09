package com.amartin.marvelapplication.ui.favorite_detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.common.adapter.ComicAdapter
import com.amartin.marvelapplication.data.database.RoomDataSource
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.Content
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.Loading
import com.amartin.marvelapplication.ui.viewer.ImageViewerActivity
import kotlinx.android.synthetic.main.activity_favourite_detail.*

class FavouriteDetailActivity: AppCompatActivity() {

    companion object {
        const val CHARACTER = "FavouriteDetailActivity:character"
    }

    private lateinit var viewModel: FavouriteDetailViewModel
    private lateinit var comicAdapter: ComicAdapter

    private fun initAdapters() {
        comicAdapter = ComicAdapter(viewModel::onComicImageClick)
        comicRecyclerView.setHasFixedSize(true)
        comicRecyclerView.adapter = comicAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_detail)

        val characterId = intent.getIntExtra(CHARACTER, -1)
        if (characterId == -1) throw IllegalStateException("Character not found")

        viewModel = ViewModelProviders.of(this,
            FavouriteDetailViewModelFactory(
                RoomDataSource(app.db), characterId))[FavouriteDetailViewModel::class.java]

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