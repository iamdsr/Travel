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
import com.iamdsr.travel.utils.AppConstants
import com.iamdsr.travel.R
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ItineraryModel

class ItineraryRecyclerAdapter(private val itemClickListener: RecyclerViewActionsInterface): ListAdapter<ItineraryModel, RecyclerView.ViewHolder>(ItineraryDiffUtilCallback()) {

    var context: Context?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            AppConstants.HOTEL_CHECK_IN_ITEM -> R.layout.layout_itinerary_hotel_item
            AppConstants.HOTEL_CHECK_IN_FIRST_ITEM -> R.layout.layout_itinerary_hotel_first_item
            AppConstants.HOTEL_CHECK_IN_LAST_ITEM -> R.layout.layout_itinerary_hotel_last_item
            AppConstants.CAR_JOURNEY_ITEM -> R.layout.layout_itinerary_car_journey_item
            AppConstants.CAR_JOURNEY_FIRST_ITEM -> R.layout.layout_itinerary_car_journey_first_item
            AppConstants.CAR_JOURNEY_LAST_ITEM -> R.layout.layout_itinerary_car_journey_last_item
            AppConstants.FLIGHT_JOURNEY_ITEM -> R.layout.layout_itinerary_plane_journey_item
            AppConstants.FLIGHT_JOURNEY_FIRST_ITEM -> R.layout.layout_itinerary_plane_journey_first_item
            AppConstants.FLIGHT_JOURNEY_LAST_ITEM -> R.layout.layout_itinerary_plane_journey_last_item
            AppConstants.TRAIN_JOURNEY_ITEM -> R.layout.layout_itinerary_train_journey_item
            AppConstants.TRAIN_JOURNEY_FIRST_ITEM -> R.layout.layout_itinerary_train_journey_first_item
            AppConstants.TRAIN_JOURNEY_LAST_ITEM -> R.layout.layout_itinerary_train_journey_last_item
            AppConstants.SIGHT_SEEING_ITEM -> R.layout.layout_itinerary_sight_seeing_item
            AppConstants.SIGHT_SEEING_FIRST_ITEM -> R.layout.layout_itinerary_sight_seeing_first_item
            AppConstants.SIGHT_SEEING_LAST_ITEM -> R.layout.layout_itinerary_sight_seeing_last_item
            else -> throw IllegalArgumentException("Invalid type")
        }
        context = parent.context
        Log.d("TAG", "onCreateViewHolder: layout ${context!!.resources.getResourceEntryName(layout)}")
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itineraryModel: ItineraryModel = getItem(position)
        (holder as ViewHolder).bindView(itineraryModel, context!!, itemClickListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private fun bindHotelItem(item: ItineraryModel, context: Context, itemClickListener: RecyclerViewActionsInterface) {
            val mTitle: TextView = itemView.findViewById(R.id.action_title)
            val mDesc: TextView = itemView.findViewById(R.id.action_desc)
            val mHotelDetails: TextView = itemView.findViewById(R.id.hotel_details)
            val mDayDate: TextView = itemView.findViewById(R.id.day_and_date)
            val mCheckIn: TextView = itemView.findViewById(R.id.check_in_time)
            val mEdit: ImageView = itemView.findViewById(R.id.edit)
            val mComplete: ImageView = itemView.findViewById(R.id.completed)

            mTitle.text = item.title
            mDesc.text = item.description
            mDayDate.text = context.getString(R.string.day_date, (item.day), item.date)
            mHotelDetails.text = context.getString(R.string.hotel_details, item.hotel_name, item.hotel_address)
            mCheckIn.text = context.getString(R.string.check_in_details, item.time)
            if (item.completed){
                mComplete.setImageResource(R.drawable.ic_radio_checked_filled)
            }
            else {
                mComplete.setImageResource(R.drawable.ic_radio_outlined)
            }
            mComplete.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })

            mEdit.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })
        }
        private fun bindJourney(item: ItineraryModel, context: Context, itemClickListener: RecyclerViewActionsInterface) {
            val mTitle: TextView = itemView.findViewById(R.id.action_title)
            val mDesc: TextView = itemView.findViewById(R.id.action_desc)
            val mFromTo: TextView = itemView.findViewById(R.id.from_to)
            val mDayDate: TextView = itemView.findViewById(R.id.day_and_date)
            val mStartTime: TextView = itemView.findViewById(R.id.start_time)
            val mEdit: ImageView = itemView.findViewById(R.id.edit)
            val mComplete: ImageView = itemView.findViewById(R.id.completed)

            mTitle.text = item.title
            mDesc.text = item.description
            mDayDate.text = context.getString(R.string.day_date, (item.day), item.date)
            mFromTo.text = context.getString(R.string.from_to_details, item.from, item.to)
            mStartTime.text = context.getString(R.string.start_at_details, item.time)
            if (item.completed){
                mComplete.setImageResource(R.drawable.ic_radio_checked_filled)
            }
            else {
                mComplete.setImageResource(R.drawable.ic_radio_outlined)
            }
            mComplete.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })

            mEdit.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })
        }
        private fun bindSightseeing(item: ItineraryModel, context: Context, itemClickListener: RecyclerViewActionsInterface) {
            val mTitle: TextView = itemView.findViewById(R.id.action_title)
            val mDesc: TextView = itemView.findViewById(R.id.action_desc)
            val mSightDetails: TextView = itemView.findViewById(R.id.sight_details)
            val mDayDate: TextView = itemView.findViewById(R.id.day_and_date)
            val mVisitTime: TextView = itemView.findViewById(R.id.visit_time)
            val mEdit: ImageView = itemView.findViewById(R.id.edit)
            val mComplete: ImageView = itemView.findViewById(R.id.completed)

            mTitle.text = item.title
            mDesc.text = item.description
            mDayDate.text = context.getString(R.string.day_date, (item.day), item.date)
            mSightDetails.text = context.getString(R.string.sight_details, item.sight_name, item.sight_address)
            mVisitTime.text = context.getString(R.string.visit_details, item.time)

            if (item.completed){
                mComplete.setImageResource(R.drawable.ic_radio_checked_filled)
            }
            else {
                mComplete.setImageResource(R.drawable.ic_radio_outlined)
            }

            mComplete.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })

            mEdit.setOnClickListener(View.OnClickListener {
                itemClickListener.onItemClick(it, absoluteAdapterPosition)
            })
        }
        fun bindView(dataModel: ItineraryModel, context: Context, itemClickListener: RecyclerViewActionsInterface) {
            when (dataModel.type) {
                AppConstants.HOTEL_CHECK_IN_ITINERARY_TYPE -> bindHotelItem(dataModel, context, itemClickListener)
                AppConstants.JOURNEY_ITINERARY_TYPE -> bindJourney(dataModel, context, itemClickListener)
                AppConstants.SIGHT_SEEING_ITINERARY_TYPE -> bindSightseeing(dataModel, context, itemClickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val itineraryModel: ItineraryModel = getItem(position)
        if (itineraryModel.type == AppConstants.HOTEL_CHECK_IN_ITINERARY_TYPE){
            if (position == 0 && itemCount == 1){
                return AppConstants.HOTEL_CHECK_IN_FIRST_ITEM
            }
            if (position == 0 && itemCount > 1){
                return AppConstants.HOTEL_CHECK_IN_LAST_ITEM
            }
            if (position > 0 && position < (itemCount-1)){
                return AppConstants.HOTEL_CHECK_IN_ITEM
            }
            if (position == (itemCount-1)){
                return AppConstants.HOTEL_CHECK_IN_FIRST_ITEM
            }
        }
        if (itineraryModel.type == AppConstants.SIGHT_SEEING_ITINERARY_TYPE){
            if (position == 0 && itemCount == 1){
                return AppConstants.SIGHT_SEEING_FIRST_ITEM
            }
            if (position == 0 && itemCount > 1){
                return AppConstants.SIGHT_SEEING_LAST_ITEM
            }
            if (position > 0 && position < (itemCount-1)){
                return AppConstants.SIGHT_SEEING_ITEM
            }
            if (position == (itemCount-1)){
                return AppConstants.SIGHT_SEEING_FIRST_ITEM
            }
        }
        if (itineraryModel.type == AppConstants.JOURNEY_ITINERARY_TYPE){
            if (itineraryModel.journey_mode == "Train"){
                if (position == 0 && itemCount == 1){
                    return AppConstants.TRAIN_JOURNEY_FIRST_ITEM
                }
                if (position == 0 && itemCount > 1){
                    return AppConstants.TRAIN_JOURNEY_LAST_ITEM
                }
                if (position > 0 && position < (itemCount-1)){
                    return AppConstants.TRAIN_JOURNEY_ITEM
                }
                if (position == (itemCount-1)){
                    return AppConstants.TRAIN_JOURNEY_FIRST_ITEM
                }
            }
            if (itineraryModel.journey_mode == "Flight"){
                if (position == 0 && itemCount == 1){
                    return AppConstants.FLIGHT_JOURNEY_FIRST_ITEM
                }
                if (position == 0 && itemCount > 1){
                    return AppConstants.FLIGHT_JOURNEY_LAST_ITEM
                }
                if (position > 0 && position < (itemCount-1)){
                    return AppConstants.FLIGHT_JOURNEY_ITEM
                }
                if (position == (itemCount-1)){
                    return AppConstants.FLIGHT_JOURNEY_FIRST_ITEM
                }
            }
            if (itineraryModel.journey_mode == "Car"){
                if (position == 0 && itemCount == 1){
                    return AppConstants.CAR_JOURNEY_FIRST_ITEM
                }
                if (position == 0 && itemCount > 1){
                    return AppConstants.CAR_JOURNEY_LAST_ITEM
                }
                if (position > 0 && position < (itemCount-1)){
                    return AppConstants.CAR_JOURNEY_ITEM
                }
                if (position == (itemCount-1)){
                    return AppConstants.CAR_JOURNEY_FIRST_ITEM
                }
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