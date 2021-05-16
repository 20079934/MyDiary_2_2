package com.w20079934.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.w20079934.helpers.readImageFromPath
import com.w20079934.models.EntryModel
import com.w20079934.mydiary_2.R
import kotlinx.android.synthetic.main.card_entry.view.*

interface EntryListener {
    fun onEntryClick(entry: EntryModel)
}

class EntryAdapter constructor(private var entries: MutableList<EntryModel>, private val listener : EntryListener) : RecyclerView.Adapter<EntryAdapter.MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_entry,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val entry = entries[holder.adapterPosition]
        holder.bind(entry, listener)
    }

    override fun getItemCount(): Int = entries.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(entry: EntryModel, listener: EntryListener) {
            itemView.dayNumber.text = "${entry.date.get("day")}-${entry.date.get("month")}-${entry.date.get("year")}"
            itemView.dayTopic.text = entry.topic
            itemView.imageIcon.setImageBitmap(readImageFromPath(itemView.context,entry.image))
            itemView.setOnClickListener {listener.onEntryClick(entry)}
        }
    }

    fun removeAt(position: Int) {
        entries.removeAt(position)
        notifyItemRemoved(position)
    }

}