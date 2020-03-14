package com.amartin.marvelapplication.ui.main

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.common.adapter.CharacterAdapter
import com.amartin.marvelapplication.data.repository.MarvelRepository
import com.amartin.marvelapplication.ui.detail.DetailActivity
import com.amartin.marvelapplication.ui.favourite.FavouriteActivity
import com.amartin.marvelapplication.ui.main.MainViewModel.UiModel.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CharacterAdapter
    val viewModel: MainViewModel by lazy {
        getViewModel { MainViewModel(marvelRepository, Dispatchers.Main) }
    }
    @Inject lateinit var marvelRepository: MarvelRepository

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_favorite -> {
                startActivity<FavouriteActivity>{}
                true
            }
            R.id.menu_about -> {
                showAbout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAbout() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.about)
        dialog.findViewById<ImageView>(R.id.imageAbout).setOnClickListener { dialog.dismiss() }
        val textView =  dialog.findViewById<TextView>(R.id.textAbout)
        textView.htmlLink(resources.getString(R.string.text_about))
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        app.component.inject(this)

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
