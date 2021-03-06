package com.iamdsr.travel.customRecyclerViewAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import java.math.RoundingMode
import java.text.DecimalFormat

class ExpenseGroupRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface) : ListAdapter<ExpenseGroupModel, ExpenseGroupRecyclerAdapter.ExpenseGroupViewHolder>(ExpenseGroupDiffUtilCallback()){

    private lateinit var context: Context

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

        private var mGroupName: TextView = itemView.findViewById(R.id.group_name)
        private var mGroupImage: ImageView = itemView.findViewById(R.id.group_image)
        private var mHighlightMessage: TextView = itemView.findViewById(R.id.get_back_owes)
        private var mMemberList: TextView = itemView.findViewById(R.id.group_member_list)

        fun bindView(model: ExpenseGroupModel, context: Context?, itemClickListener: RecyclerViewActionsInterface) {

            if (context != null){
                Glide
                    .with(context)
                    .load(model.group_image_url)
                    .placeholder(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mGroupImage)
            }
            mGroupName.text = model.name

            // set highlight message
            var msg = ""
            for ((memberID, amt) in model.members_payment_status){
                if (memberID.split("-")[0] == FirebaseAuth.getInstance().currentUser!!.uid) {
                    msg += if (memberID.split("-")[1] == "Borrowed"){
                        "You borrowed ???${roundOffDecimal(amt)} in total\n"
                    } else{
                        "You lent ???${roundOffDecimal(amt)} in total\n"
                    }
                }
            }
            mHighlightMessage.text = msg



            itemView.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })
        }

        private fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
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
