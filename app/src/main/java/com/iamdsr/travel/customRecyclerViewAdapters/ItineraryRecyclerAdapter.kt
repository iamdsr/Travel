package com.iamdsr.travel.customRecyclerViewAdapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iamdsr.travel.utils.AppConstants
import com.iamdsr.travel.R
import com.iamdsr.travel.models.ItineraryModel

class ItineraryRecyclerAdapter: ListAdapter<ItineraryModel, RecyclerView.ViewHolder>(ItineraryDiffUtilCallback()) {

    var context: Context?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            AppConstants.HOTEL_CHECK_IN_ITEM -> R.layout.layout_itinerary_hotel_item
            AppConstants.HOTEL_CHECK_IN_FIRST_ITEM -> R.layout.layout_itinerary_hotel_first_item
            AppConstants.HOTEL_CHECK_IN_LAST_ITEM -> R.layout.layout_itinerary_hotel_last_item
            else -> throw IllegalArgumentException("Invalid type")
        }
        context = parent.context
        Log.d("TAG", "onCreateViewHolder: layout ${context!!.resources.getResourceEntryName(layout)}")
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itineraryModel: ItineraryModel = getItem(position)
        (holder as ViewHolder).bindView(itineraryModel, context!!)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private fun bindHotelItem(item: ItineraryModel, context: Context) {
            val mTitle: TextView = itemView.findViewById(R.id.action_title)
            val mDesc: TextView = itemView.findViewById(R.id.action_desc)
            val mHotelDetails: TextView = itemView.findViewById(R.id.hotel_details)
            val mDayDate: TextView = itemView.findViewById(R.id.day_and_date)
            val mCheckIn: TextView = itemView.findViewById(R.id.check_in_time)
            mTitle.text = item.title
            mDesc.text = item.description
            mDayDate.text = context.getString(R.string.day_date, (absoluteAdapterPosition+1), item.date)
            mHotelDetails.text = context.getString(R.string.hotel_details, item.hotel_name, item.hotel_address)
            mCheckIn.text = context.getString(R.string.check_in_details, item.time)
        }
        private fun bindJourney(item: ItineraryModel, context: Context) {

        }
        private fun bindSightseeing(item: ItineraryModel, context: Context) {

        }
        fun bindView(dataModel: ItineraryModel, context: Context) {
            when (dataModel.type) {
                dataModel.type -> bindHotelItem(dataModel, context)
                dataModel.type -> bindJourney(dataModel, context)
                dataModel.type -> bindSightseeing(dataModel, context)
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        val itineraryModel: ItineraryModel = getItem(position)
        if (itineraryModel.type == "HOTEL_CHECK_IN"){
            if (position == (itemCount-1)){
                return AppConstants.HOTEL_CHECK_IN_FIRST_ITEM
            }
            if (position == 0){
                return AppConstants.HOTEL_CHECK_IN_LAST_ITEM
            }
            if (position < (itemCount-1)){
                return AppConstants.HOTEL_CHECK_IN_ITEM
            }
        }
        return -1
    }

}

class ItineraryDiffUtilCallback : DiffUtil.ItemCallback<ItineraryModel>(){
    override fun areItemsTheSame(oldItem: ItineraryModel, newItem: ItineraryModel): Boolean {
        return oldItem.itinerary_id == newItem.itinerary_id
    }

    override fun areContentsTheSame(oldItem: ItineraryModel, newItem: ItineraryModel): Boolean {
        return oldItem == newItem
    }

}