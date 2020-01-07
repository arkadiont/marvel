package com.amartin.marvelapplication.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.api.MarvelService
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.data.impl.MarvelCharacterRemoteDataSource
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.ui.detail.DetailViewModel.*
import com.amartin.marvelapplication.ui.detail.DetailViewModel.UiCharacterModel.*
import kotlinx.android.synthetic.main.activity_detail.*
import java.lang.IllegalStateException

class DetailActivity : AppCompatActivity() {

    companion object {
        const val CHARACTER = "DetailActivity:character"
    }

    private lateinit var viewModel: DetailViewModel
    private lateinit var comicAdapter: ComicAdapter
    private lateinit var urlAdapter: UrlAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val characterId = intent.getIntExtra(CHARACTER, -1)
        if (characterId == -1) throw IllegalStateException("Character not found")

        viewModel = ViewModelProviders.of(this,
            DetailViewModelFactory(MarvelRepository(
                MarvelCharacterRemoteDataSource(MarvelService.create(
                    Credentials.privateKey,
                    Credentials.publicKey
                ))), characterId))[DetailViewModel::class.java]

        initAdapters()

        viewModel.characterModel.observe(this, Observer(::updateCharacterUi))
        viewModel.comicModel.observe(this, Observer(::updateComicsUi))
        viewModel.error.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { toast(it) }
        })
        viewModel.navigation.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
            }
        })
    }

    private fun initAdapters() {
        comicAdapter = ComicAdapter()
        comicRecyclerView.setHasFixedSize(true)
        comicRecyclerView.adapter = comicAdapter

        urlAdapter = UrlAdapter(viewModel::onUrlClick)
        urlRecyclerView.adapter = urlAdapter
        urlRecyclerView.setHasFixedSize(true)
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