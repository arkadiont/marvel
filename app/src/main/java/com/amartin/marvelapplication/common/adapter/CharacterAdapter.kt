package com.amartin.marvelapplication.common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amartin.marvelapplication.R
import com.amartin.marvelapplication.common.*
import com.amartin.marvelapplication.data.model.CharacterData
import kotlinx.android.synthetic.main.view_character.view.*
import kotlin.properties.Delegates

class CharacterAdapter(private val listener: (CharacterData) -> Unit):
    RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    var characters: List<CharacterData> by Delegates.observable(emptyList()) { _, old, new ->
        DiffUtil.calculateDiff(object : DiffUtil.Callback(){
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition].id == new[newItemPosition].id

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition] == new[newItemPosition]
        }).dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.view_character, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = characters.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val character = characters[position]
        holder.bind(character)
        holder.itemView.setOnClickListener{ listener(character) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(character: CharacterData) {
            itemView.characterName.text = character.name
            itemView.characterImage.loadUrl(character.thumbnail.getUrl())
        }
    }
}
