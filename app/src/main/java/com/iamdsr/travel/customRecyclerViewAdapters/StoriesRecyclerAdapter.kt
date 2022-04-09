package com.iamdsr.travel.customRecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.StoriesModel

class StoriesRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface) : ListAdapter<StoriesModel, StoriesRecyclerAdapter.StoriesViewHolder>(MyDiffCallback()) {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return StoriesViewHolder(inflater.inflate(R.layout.layout_stories_item, parent, false))
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val model: StoriesModel = getItem(position)
        holder.bindView(model, context, itemClickListener)
    }

    class StoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(
            model: StoriesModel,
            context: Context?,
            itemClickListener: RecyclerViewActionsInterface
        ) {

            itemView.setOnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            }
        }
    }

}

class MyDiffCallback : DiffUtil.ItemCallback<StoriesModel>(){
    override fun areItemsTheSame(oldItem: StoriesModel, newItem: StoriesModel): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: StoriesModel, newItem: StoriesModel): Boolean {
        return oldItem == newItem
    }
}