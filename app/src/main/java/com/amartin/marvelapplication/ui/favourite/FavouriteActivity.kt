package com.amartin.marvelapplication.ui.favourite

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.Event
import com.amartin.marvelapplication.common.adapter.CharacterAdapter
import com.amartin.marvelapplication.common.startActivity
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailActivity
import com.amartin.marvelapplication.ui.favorite_detail.FavouriteDetailActivity.Companion.CHARACTER
import com.amartin.marvelapplication.ui.favourite.FavouriteViewModel.UiModel.Content
import com.amartin.marvelapplication.ui.favourite.FavouriteViewModel.UiModel.Loading
import kotlinx.android.synthetic.main.activity_favourite.*
import org.koin.android.scope.currentScope
import org.koin.android.viewmodel.ext.android.viewModel

class FavouriteActivity : AppCompatActivity() {

    private lateinit var adapter: CharacterAdapter
    private val viewModel: FavouriteViewModel by currentScope.viewModel(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        adapter = CharacterAdapter(viewModel::onCharacterClick)
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)

        viewModel.model.observe(this, Observer(::updateUi))
        viewModel.navigate.observe(this, Observer(::navigate))
    }

    private fun navigate(event: Event<Int>) {
        event.getContentIfNotHandled()?.let {
            startActivity<FavouriteDetailActivity>{
                putExtra(CHARACTER, it)
            }
        }
    }

    private fun updateUi(model: FavouriteViewModel.UiModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is Content -> {
                emptyList.visibility = if (model.characters.isEmpty()) View.VISIBLE else View.GONE
                adapter.characters = model.characters
            }
            else -> return
        }
    }
}