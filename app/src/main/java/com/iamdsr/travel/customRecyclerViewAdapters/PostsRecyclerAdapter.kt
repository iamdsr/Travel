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
import com.iamdsr.travel.models.PostsModel

class PostsRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface) : ListAdapter<PostsModel, PostsRecyclerAdapter.StoriesViewHolder>(MyDiffCallback()) {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return StoriesViewHolder(inflater.inflate(R.layout.layout_stories_item, parent, false))
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val model: PostsModel = getItem(position)
        holder.bindView(model, context, itemClickListener)
    }

    class StoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(
            model: PostsModel,
            context: Context?,
            itemClickListener: RecyclerViewActionsInterface
        ) {

            itemView.setOnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            }
        }
    }

}

class MyDiffCallback : DiffUtil.ItemCallback<PostsModel>(){
    override fun areItemsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
        return oldItem == newItem
    }
}