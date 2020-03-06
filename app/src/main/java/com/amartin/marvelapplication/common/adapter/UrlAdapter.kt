package com.amartin.marvelapplication.common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.inflate
import com.amartin.marvelapplication.data.model.Url
import kotlinx.android.synthetic.main.view_url.view.*

class UrlAdapter(private val listener: (Url) -> Unit) : RecyclerView.Adapter<UrlAdapter.ViewHolder>() {

    private var urls = mutableListOf<Url>()

    fun update(url: List<Url>) {
        urls = url.toMutableList()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(url: Url) {
            itemView.url.text = url.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.view_url, false)
        return ViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = urls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = urls[position]
        holder.bind(url)
        holder.itemView.setOnClickListener { listener(url) }
    }
}
