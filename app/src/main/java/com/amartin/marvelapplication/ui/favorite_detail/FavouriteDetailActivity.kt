package com.amartin.marvelapplication.ui.favorite_detail

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.api.model.ScrappedCharacter
import com.amartin.marvelapplication.api.model.ScrappedElement
import com.amartin.marvelapplication.api.model.ScrappedPowerGrid
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.common.adapter.ComicAdapter
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.Content
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.Loading
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailViewModel.UiModel.ScrapingContent
import com.amartin.marvelapplication.ui.viewer.ImageViewerActivity
import kotlinx.android.synthetic.main.activity_favourite_detail.*
import kotlinx.android.synthetic.main.more_info_bio_data_element.view.*
import kotlinx.android.synthetic.main.view_more_info.view.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavouriteDetailActivity: AppCompatActivity() {

    companion object {
        const val CHARACTER = "FavouriteDetailActivity:character"
    }
    private val viewModel: FavouriteDetailViewModel by currentScope.viewModel(this) {
        parametersOf(intent.getIntExtra(CHARACTER, -1))
    }
    private lateinit var comicAdapter: ComicAdapter

    private fun initAdapters() {
        comicAdapter = ComicAdapter(viewModel::onComicImageClick)
        comicRecyclerView.setHasFixedSize(true)
        comicRecyclerView.adapter = comicAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_detail)

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
                    tryGetMoreInfo.setOnClickListener {
                        viewModel.onMoreInfoClick(this)
                        // one click is enough
//                        tryGetMoreInfo.isEnabled = false
                    }
                    characterDetailImage.loadUrl(thumbnail.getUrl())
                    characterDetailImage.setOnClickListener { viewModel.onCharacterClick(this) }
                    characterDescription.text =
                        if (description.isNotBlank()) description else getString(R.string.not_available)
                }
                comicAdapter.updateComicList(model.characterComicData.comicData)
            }
            is ScrapingContent -> {
                println("scrapped:\n${model.scraping}")
                if (model.scraping.hasValues()) {
                    viewMoreInfo.visibility = View.VISIBLE
                    updateUiScrappedElementList(model.scraping.scrappedElementList)
                    updateUiScrappedPowerGridList(model.scraping.scrappedPowerGridList)
                    model.scraping.scrappedCharacter?.let {
                        updateUiScrappedCharacter(it)
                    }
                }else {
                    snackBar("Can´t scrapping info...")
                }
            }
            else -> return
        }
    }

    private fun updateUiScrappedCharacter(character: ScrappedCharacter) {
        character.physicalTraits?.let {
            viewMoreInfo.physical_traits.visibility = View.VISIBLE
            viewMoreInfo.findViewById<TextView>(R.id.height).text = it.height
            viewMoreInfo.findViewById<TextView>(R.id.weight).text = it.weight
            viewMoreInfo.findViewById<TextView>(R.id.eyes).text = it.eyeColor
            viewMoreInfo.findViewById<TextView>(R.id.hair).text = it.hairColor
        }
        if (character.bioData.isNotEmpty()) {
            viewMoreInfo.bio_data.visibility = View.VISIBLE
            val parent = viewMoreInfo.parent_container
            character.bioData.forEach {
                val child = layoutInflater.inflate(R.layout.more_info_bio_data_element, parent, false)
                child.key.text = it.key
                child.value.html(it.value)
                parent.addView(child)
            }
        }
    }

    private fun updateUiScrappedPowerGridList(elements: List<ScrappedPowerGrid>) {
        if (elements.isNotEmpty()) {
            viewMoreInfo.findViewById<LinearLayout>(R.id.power_stats).visibility = View.VISIBLE
            elements.forEach {
                val id = when (it.label) {
                    "speed" -> R.id.speed
                    "energy"-> R.id.energy
                    "strength" -> R.id.strength
                    "durability" -> R.id.durability
                    "intelligence" -> R.id.intelligence
                    "fighting_skills" -> R.id.fighting_skills
                    else -> -1
                }
                if (id != -1) {
                    viewMoreInfo.findViewById<ProgressBar>(id).progress = it.powerRating.toInt()
                }
            }
        }
    }

    private fun updateUiScrappedElementList(elements: List<ScrappedElement>) {
        if (elements.isNotEmpty()) {
            viewMoreInfo.findViewById<TextView>(R.id.info).visibility = View.VISIBLE
            viewMoreInfo.findViewById<TextView>(R.id.info).html(elements.joinToString(separator = ""))
        }
    }
}