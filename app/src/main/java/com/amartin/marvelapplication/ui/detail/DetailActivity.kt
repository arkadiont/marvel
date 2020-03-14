package com.amartin.marvelapplication.ui.detail

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.api.MarvelService
import com.amartin.marvelapplication.api.YandexService
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.common.adapter.ComicAdapter
import com.amartin.marvelapplication.common.adapter.UrlAdapter
import com.amartin.marvelapplication.data.database.RoomDataSource
import com.amartin.marvelapplication.data.impl.MarvelCharacterRemoteMarvelDataSource
import com.amartin.marvelapplication.data.impl.PermissionCheckerImpl
import com.amartin.marvelapplication.data.impl.PlayServicesLocationDataSource
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.data.repository.RegionRepository
import com.amartin.marvelapplication.ui.detail.DetailViewModel.*
import com.amartin.marvelapplication.ui.detail.DetailViewModel.Navigate.ActivityImageViewer
import com.amartin.marvelapplication.ui.detail.DetailViewModel.UiCharacterModel.*
import com.amartin.marvelapplication.ui.viewer.ImageViewerActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalStateException

class DetailActivity : AppCompatActivity() {

    companion object {
        const val CHARACTER = "DetailActivity:character"
    }

    private lateinit var viewModel: DetailViewModel
    private lateinit var comicAdapter: ComicAdapter
    private lateinit var urlAdapter: UrlAdapter

    private val coarsePermissionRequester =
        PermissionRequester(this, ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val characterId = intent.getIntExtra(CHARACTER, -1)
        if (characterId == -1) throw IllegalStateException("Character not found")

        viewModel = ViewModelProviders.of(this,
            DetailViewModelFactory(
                RegionRepository(
                    PlayServicesLocationDataSource(app),
                    PermissionCheckerImpl(app)),
                MarvelRepository(
                    MarvelCharacterRemoteMarvelDataSource(
                        MarvelService.create(Credentials.privateKey, Credentials.publicKey)),
                    RoomDataSource(app.db)
                ),
                YandexService.create(Credentials.yandexApikey),
                characterId, Dispatchers.Main))[DetailViewModel::class.java]

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
        }
    }
}