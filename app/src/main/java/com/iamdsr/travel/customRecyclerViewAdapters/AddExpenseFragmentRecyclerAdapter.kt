package com.iamdsr.travel.customRecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ExpenseModel
import java.lang.Exception
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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

        private var mDate: TextView = itemView.findViewById(R.id.date)
        private var mExpenseIcon: ImageView = itemView.findViewById(R.id.icon)
        private var mTitle: TextView = itemView.findViewById(R.id.expense_title)
        private var mAmount: TextView = itemView.findViewById(R.id.paid_amount)
        private var mLentBorrowed: TextView = itemView.findViewById(R.id.lent_borrowed_info)

        fun bindView(
            model: ExpenseModel,
            context: Context,
            itemClickListener: RecyclerViewActionsInterface
        ) {
            val date = model.expense_create_date
            mDate.text = getDate(date)
            mExpenseIcon.setImageResource(getExpenseIcon(model.name))
            mTitle.text = model.name
            mAmount.text = getPaidByInfo(model.paid_by_name, model.paid_by_ID, model.amount_paid)
            mLentBorrowed.text = getLentBorrowedInfo(model.expense_calculation_map)

        }

        private fun roundOffDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }

        private fun getLentBorrowedInfo(map: MutableMap<String, Double>): String{
            var text = ""
            for ((id, amount) in map){
                if (id.split("-")[0] == FirebaseAuth.getInstance().currentUser!!.uid){
                    text = "You ${id.split("-")[1]} ₹${roundOffDecimal(amount)}"
                }
            }
            return text
        }

        private fun getPaidByInfo(name: String, id: String, amount: Double): String{
            return if (id == FirebaseAuth.getInstance().currentUser!!.uid){
                "You paid ₹$amount in total"
            } else {
                "$name paid ₹$amount in total"
            }
        }
        private fun getDate(date: String): String{
            val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val result: Date?
            var outputDate: String = ""
            try {
                result = df.parse(date)
                val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("GMT");
                outputDate = sdf.format(result!!)
            }
            catch (e: Exception){

            }
            return outputDate.replace(" ", "\n")
        }

        private fun getExpenseIcon(title: String): Int {
            val checkTitle = title.lowercase(Locale.getDefault())
            if (checkTitle.contains("Hotel", ignoreCase = true) ||
                checkTitle.contains("House", ignoreCase = true) ||
                checkTitle.contains("Accommodation", ignoreCase = true)){
                return R.drawable.ic_hotel_stay
            }
            else if (checkTitle.contains("Food", ignoreCase = true) ||
                checkTitle.contains("Snacks", ignoreCase = true) ||
                checkTitle.contains("breakfast", ignoreCase = true) ||
                checkTitle.contains("Dinner", ignoreCase = true) ||
                checkTitle.contains("Lunch", ignoreCase = true)){
                return R.drawable.ic_food
            }
            else if (checkTitle.contains("Drinks", ignoreCase = true) ||
                checkTitle.contains("Beer", ignoreCase = true) ||
                checkTitle.contains("Vodka", ignoreCase = true) ||
                checkTitle.contains("Wine", ignoreCase = true) ||
                checkTitle.contains("Champagne", ignoreCase = true)){
                return R.drawable.ic_drinks
            }
            else if (checkTitle.contains("Car", ignoreCase = true) ||
                checkTitle.contains("Journey", ignoreCase = true) ||
                checkTitle.contains("Travel", ignoreCase = true) ||
                checkTitle.contains("Return", ignoreCase = true) ||
                checkTitle.contains("Bus", ignoreCase = true)||
                checkTitle.contains("Train", ignoreCase = true) ||
                checkTitle.contains("Flight", ignoreCase = true) ||
                checkTitle.contains("Ticket", ignoreCase = true) ||
                checkTitle.contains("Petrol", ignoreCase = true) ||
                checkTitle.contains("Diesel", ignoreCase = true)){
                return R.drawable.ic_journey
            }
            else{
                return R.drawable.ic_round_feed
            }
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