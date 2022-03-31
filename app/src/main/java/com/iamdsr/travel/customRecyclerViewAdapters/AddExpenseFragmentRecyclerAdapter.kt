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
import com.iamdsr.travel.models.ExpenseModel

class AddExpenseFragmentRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface):
    ListAdapter<ExpenseModel, AddExpenseFragmentRecyclerAdapter.ExpenseViewHolder>(ExpenseDiffUtilCallback()) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return ExpenseViewHolder(
            inflater.inflate(
                R.layout.layout_expense_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val model: ExpenseModel = getItem(position)
        holder.bindView(model, context, itemClickListener)
    }

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(
            model: ExpenseModel,
            context: Context,
            itemClickListener: RecyclerViewActionsInterface
        ) {


        }

    }
}

class ExpenseDiffUtilCallback : DiffUtil.ItemCallback<ExpenseModel>() {
    override fun areItemsTheSame(oldItem: ExpenseModel, newItem: ExpenseModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ExpenseModel, newItem: ExpenseModel): Boolean {
        return oldItem.id == newItem.id
    }

}