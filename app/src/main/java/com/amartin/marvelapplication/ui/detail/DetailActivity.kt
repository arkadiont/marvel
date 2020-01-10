package com.amartin.marvelapplication.ui.detail

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.api.MarvelService
import com.amartin.marvelapplication.api.YandexService
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.data.impl.MarvelCharacterRemoteMarvelDataSource
import com.amartin.marvelapplication.data.impl.PermissionCheckerImpl
import com.amartin.marvelapplication.data.impl.PlayServicesLocationDataSource
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.data.repository.RegionRepository
import com.amartin.marvelapplication.ui.detail.DetailViewModel.*
import com.amartin.marvelapplication.ui.detail.DetailViewModel.Navigate.ActivityImageViewer
import com.amartin.marvelapplication.ui.detail.DetailViewModel.Navigate.OpenActionView
import com.amartin.marvelapplication.ui.detail.DetailViewModel.UiCharacterModel.*
import com.amartin.marvelapplication.ui.viewer.ImageViewer
import kotlinx.android.synthetic.main.activity_detail.*
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
                        MarvelService.create(Credentials.privateKey, Credentials.publicKey))),
                YandexService.create(Credentials.yandexApikey),
                characterId))[DetailViewModel::class.java]

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
        comicAdapter = ComicAdapter(viewModel::onComicImageClick)
        comicRecyclerView.setHasFixedSize(true)
        comicRecyclerView.adapter = comicAdapter

        urlAdapter = UrlAdapter(viewModel::onUrlClick)
        urlRecyclerView.adapter = urlAdapter
        urlRecyclerView.setHasFixedSize(true)
    }

    private fun navigate(model: Navigate) {
        when (model) {
            is OpenActionView -> model.url.getContentIfNotHandled()?.let {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
            }
            is ActivityImageViewer -> model.url.getContentIfNotHandled()?.let {
                startActivity<ImageViewer>{
                    putExtra(ImageViewer.IMAGE_URL, it)
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
            is CharacterContent -> with(model.character) {
                characterDetailToolbar.title = name
                characterDetailImage.loadUrl(thumbnail.getUrl())
                characterDescription.text =
                    if (description.isNotBlank()) description else getString(R.string.not_available)
                urlAdapter.update(urls)
            }
        }
    }
}