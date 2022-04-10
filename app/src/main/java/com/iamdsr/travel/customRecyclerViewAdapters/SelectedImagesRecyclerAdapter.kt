package com.iamdsr.travel.customRecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.SelectedImagesModel
import kotlinx.android.synthetic.main.layout_selected_image_item.view.*

class SelectedImagesRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface) :
    ListAdapter<SelectedImagesModel, SelectedImagesRecyclerAdapter.SelectedItemViewHolder>(SelectedItemDIffCallback()) {

    var context: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return SelectedItemViewHolder(inflater.inflate(R.layout.layout_selected_image_item, parent, false))
    }

    override fun onBindViewHolder(holder: SelectedItemViewHolder, position: Int) {
        val model: SelectedImagesModel = getItem(position)
        holder.bindView(model, context, itemClickListener)
    }

    class SelectedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(
            model: SelectedImagesModel,
            context: Context?,
            itemClickListener: RecyclerViewActionsInterface
        ) {
            itemView.image.setImageURI(model.uri)
            /*if (context!=null){
                Glide
                    .with(context)
                    .load(model.url)
                    .placeholder(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.findViewById(R.id.image))
            }*/

            itemView.delete.setOnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            }
        }
    }
}

class SelectedItemDIffCallback: DiffUtil.ItemCallback<SelectedImagesModel>(){
    override fun areItemsTheSame(oldItem: SelectedImagesModel, newItem: SelectedImagesModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SelectedImagesModel, newItem: SelectedImagesModel): Boolean {
        return oldItem == newItem
    }
}

