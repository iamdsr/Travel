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
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.TripModel

class ExpenseGroupRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface) : ListAdapter<ExpenseGroupModel, ExpenseGroupRecyclerAdapter.ExpenseGroupViewHolder>(ExpenseGroupDiffUtilCallback()){

    var context: Context?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseGroupRecyclerAdapter.ExpenseGroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return ExpenseGroupViewHolder(inflater.inflate(R.layout.layout_expense_group_item, parent,false))
    }

    override fun onBindViewHolder(holder: ExpenseGroupRecyclerAdapter.ExpenseGroupViewHolder, position: Int) {
        val model: ExpenseGroupModel = getItem(position)
        holder.bindView(model, context, itemClickListener)
    }

    class ExpenseGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindView(model: ExpenseGroupModel, context: Context?, itemClickListener: RecyclerViewActionsInterface) {


        }

    }
}

class ExpenseGroupDiffUtilCallback: DiffUtil.ItemCallback<ExpenseGroupModel>(){
    override fun areItemsTheSame(oldItem: ExpenseGroupModel, newItem: ExpenseGroupModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExpenseGroupModel, newItem: ExpenseGroupModel): Boolean {
        return oldItem == newItem
    }

}
