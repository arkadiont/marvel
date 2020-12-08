package com.amartin.marvelapplication.ui.detail

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.common.adapter.ComicAdapter
import com.amartin.marvelapplication.common.adapter.UrlAdapter
import com.amartin.marvelapplication.ui.detail.DetailViewModel.*
import com.amartin.marvelapplication.ui.detail.DetailViewModel.Navigate.ActivityImageViewer
import com.amartin.marvelapplication.ui.detail.DetailViewModel.UiCharacterModel.*
import com.amartin.marvelapplication.ui.viewer.ImageViewerActivity
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {

    companion object {
        const val CHARACTER = "DetailActivity:character"
    }
    private lateinit var comicAdapter: ComicAdapter
    private lateinit var urlAdapter: UrlAdapter
    private val viewModel: DetailViewModel by currentScope.viewModel(this) {
        parametersOf(intent.getIntExtra(CHARACTER, -1))
    }

    private val coarsePermissionRequester =
        PermissionRequester(this, ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        initAdapters()

        coarsePermissionRequester.request {
            translateAction.isEnabled = it
        }

        translateAction.setOnClickListener{
            viewModel.onTranslateButtonClick(characterDescription.text.toString())
        }

        viewModel.characterModel.observe(this, Observer(::updateCharacterUi))
        viewModel.comicModel.observe(this, Observer(::updateComicsUi))
        viewModel.translateModel.observe(this, Observer(::updateTranslateUi))
        viewModel.navigation.observe(this, Observer(::navigate))

        viewModel.error.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { toast(it) }
        })
    }

    private fun initAdapters() {
        comicAdapter =
            ComicAdapter(viewModel::onComicImageClick)
        comicRecyclerView.setHasFixedSize(true)
        comicRecyclerView.adapter = comicAdapter

        urlAdapter = UrlAdapter()
        urlRecyclerView.adapter = urlAdapter
        urlRecyclerView.setHasFixedSize(true)
    }

    private fun navigate(model: Navigate) {
        when (model) {
            is ActivityImageViewer -> model.url.getContentIfNotHandled()?.let {
                startActivity<ImageViewerActivity>{
                    putExtra(ImageViewerActivity.IMAGE_URL, it)
                }
            }
        }
    }

    private fun updateTranslateUi(event: Event<String>) {
        event.getContentIfNotHandled()?.let {
            translated.text = it
            translated.visibility = View.VISIBLE
            powerByYandex.visibility = View.VISIBLE
            translateAction.isEnabled = false
        }
    }

    private fun updateComicsUi(model: UiComicModel) {
        progress.visibility = if (model == UiComicModel.Loading) View.VISIBLE else View.GONE
        when (model) {
            is UiComicModel.ComicsContent -> comicAdapter.updateComicList(model.comics)
            else -> return
        }
    }

    private fun updateCharacterUi(model: UiCharacterModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is CharacterContent -> {
                val icon = if (model.isFavourite) R.drawable.ic_favorite_on else R.drawable.ic_favorite_off
                with(model.character) {
                    characterDetailToolbar.title = name
                    characterDetailImage.loadUrl(thumbnail.getUrl())
                    characterDetailImage.setOnClickListener { viewModel.onCharacterImageClick(this) }
                    characterDescription.text =
                        if (description.isNotBlank()) description else getString(R.string.not_available)
                    urlAdapter.update(urls)
                    characterDetailFavorite.setImageDrawable(getDrawable(icon))
                    characterDetailFavorite.setOnClickListener {
                        viewModel.characterFavoriteClick(model.isFavourite, model.character)
                    }
                }
            }
            else -> return
        }
    }
}