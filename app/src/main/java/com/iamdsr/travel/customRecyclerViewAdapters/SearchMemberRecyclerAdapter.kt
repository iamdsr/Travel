package com.iamdsr.travel.customRecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.UserModel

class SearchMemberRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface): ListAdapter<UserModel, SearchMemberRecyclerAdapter.UsersViewHolder>(UsersDiffUtilCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return UsersViewHolder(inflater.inflate(R.layout.layout_search_user_item, parent, false))
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val model: UserModel = getItem(position)
        holder.bindView(model, context, itemClickListener)
    }

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mUsername: TextView = itemView.findViewById(R.id.user_name)
        fun bindView(model: UserModel, context: Context, itemClickListener: RecyclerViewActionsInterface) {
//            if (context != null){
//                Glide
//                    .with(context)
//                    .load(model.group_image_url)
//                    .placeholder(R.drawable.placeholder_image)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(mGroupImage)
//            }

            mUsername.text = model.full_name
            itemView.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })
        }

    }
}

class UsersDiffUtilCallback: DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem == newItem
    }

}