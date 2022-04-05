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
import com.iamdsr.travel.models.UserModel

class GroupSettingsRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface) :
    ListAdapter<UserModel, GroupSettingsRecyclerAdapter.GroupSettingsViewHolder>(DiffCallback()) {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupSettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return GroupSettingsViewHolder(inflater.inflate(R.layout.layout_search_user_item, parent, false))
    }

    override fun onBindViewHolder(holder: GroupSettingsViewHolder, position: Int) {
        val model: UserModel = getItem(position)
        holder.bindView(model, context, itemClickListener)
    }

    class GroupSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindView(
            model: UserModel,
            context: Context?,
            itemClickListener: RecyclerViewActionsInterface
        ) {

            itemView.setOnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            }
        }
    }

}

class DiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem == newItem
    }

}