package com.amartin.marvelapplication.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.api.MarvelService
import com.amartin.marvelapplication.common.Credentials
import com.amartin.marvelapplication.common.startActivity
import com.amartin.marvelapplication.common.toast
import com.amartin.marvelapplication.data.impl.MarvelCharacterRemoteDataSource
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.ui.detail.DetailActivity
import com.amartin.marvelapplication.ui.main.MainViewModel.UiModel.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CharacterAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this,
            MainViewModelFactory(MarvelRepository(
                MarvelCharacterRemoteDataSource(MarvelService.create(
                    Credentials.privateKey,
                    Credentials.publicKey)))))[MainViewModel::class.java]

        adapter = CharacterAdapter(viewModel::onCharacterClick)
        setupRecycler()

        viewModel.model.observe(this, Observer(::updateUi))
        viewModel.navigation.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                startActivity<DetailActivity> {
                    putExtra(DetailActivity.CHARACTER, it.id)
                }
            }
        })
        viewModel.error.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { toast(it) }
        })
    }

    private fun updateUi(model: MainViewModel.UiModel) {
        progress.visibility = if (model == Loading) View.VISIBLE else View.GONE
        when (model) {
            is Content -> adapter.characters = adapter.characters + model.characters
        }
    }

    private fun setupRecycler() {
        recycler.adapter = adapter
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        setupScrollListener()
    }

    private fun setupScrollListener() {
        val layoutManager = recycler.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val characterSize = adapter.characters.size

                viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount, characterSize)
            }
        })
    }
}
