package com.amartin.marvelapplication.ui.favourite

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.adapter.CharacterAdapter
import com.amartin.marvelapplication.common.app
import com.amartin.marvelapplication.common.startActivity
import com.amartin.marvelapplication.data.database.RoomDataSource
import com.amartin.marvelapplication.ui.favourite.FavouriteViewModel.UiModel.Content
import com.amartin.marvelapplication.ui.favourite.FavouriteViewModel.UiModel.Loading
import com.amartin.marvelapplication.ui.viewer.ImageViewerActivity
import kotlinx.android.synthetic.main.activity_favourite.*

class FavouriteActivity : AppCompatActivity() {

    private lateinit var adapter: CharacterAdapter
    private lateinit var viewModel: FavouriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        viewModel = ViewModelProviders.of(this,
            FavouriteViewModelFactory(RoomDataSource(app.db)))[FavouriteViewModel::class.java]

        adapter = CharacterAdapter(viewModel::onCharacterClick)
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)

        viewModel.model.observe(this, Observer(::updateUi))
        viewModel.navigate.observe(this, Observer(::navigate))
    }

    private fun navigate(event: Event<String>) {
        event.getContentIfNotHandled()?.let {
            startActivity<ImageViewerActivity>{
                putExtra(ImageViewerActivity.IMAGE_URL, it)
            }
        }
    }

    private fun updateUi(model: FavouriteViewModel.UiModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is Content -> adapter.characters = model.characters
        }
    }
}