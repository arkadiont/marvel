package com.amartin.marvelapplication.common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.getUrl
import com.amartin.marvelapplication.common.inflate
import com.amartin.marvelapplication.common.loadUrl
import com.amartin.marvelapplication.data.model.ComicData
import kotlinx.android.synthetic.main.view_comic.view.*

class ComicAdapter(private val listener: (ComicData) -> Unit) : RecyclerView.Adapter<ComicAdapter.ViewHolder>() {

    private var comics = mutableListOf<ComicData>()

    fun updateComicList(comics: List<ComicData>) {
        this.comics = comics.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.view_comic, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = comics.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comic = comics[position]
        holder.bind(comic)
        holder.itemView.comicImage.setOnClickListener { listener(comic) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(comic: ComicData) {
            itemView.comicImage.loadUrl(comic.thumbnail.getUrl())
            itemView.comicName.text = comic.title
        }
    }
}