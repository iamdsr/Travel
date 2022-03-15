package com.iamdsr.travel.customRecyclerViewAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iamdsr.travel.Interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.R
import com.iamdsr.travel.models.TripModel

class PlannedTripRecyclerAdapter : ListAdapter<TripModel, PlannedTripRecyclerAdapter.TripsViewHolder>(TripsDiffUtilCallback()){

    var context: Context?= null
    val recyclerViewActionsInterface: RecyclerViewActionsInterface?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return TripsViewHolder(inflater.inflate(R.layout.layout_trips_item, parent,false))
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        val tripModel: TripModel = getItem(position)
        holder.bindView(tripModel, context)
    }

    class TripsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTitle: TextView = itemView.findViewById(R.id.trip_title)
        private val mDesc: TextView = itemView.findViewById(R.id.trip_desc)
        private val mTotalPax: TextView = itemView.findViewById(R.id.total_person)
        private val mJDate: TextView = itemView.findViewById(R.id.journey_date)
        private val mFrom: TextView = itemView.findViewById(R.id.place_from)
        private val mRDate: TextView = itemView.findViewById(R.id.return_date)
        private val mTo: TextView = itemView.findViewById(R.id.place_to)
        private val mDuration: TextView = itemView.findViewById(R.id.total_duration)
        private val mUpdate: TextView = itemView.findViewById(R.id.update)
        private val mDelete: TextView = itemView.findViewById(R.id.delete)

        fun bindView(tripModel: TripModel, context: Context?) {
            mTitle.text = tripModel.trip_title
            mDesc.text = tripModel.trip_desc
            mJDate.text = tripModel.journey_date
            mRDate.text = tripModel.return_date
            mFrom.text = tripModel.place_from
            mTo.text = tripModel.place_to
            mTotalPax.text = tripModel.total_heads.toString()
            mDuration.text = tripModel.duration_in_days.toString()
        }
    }

}

class TripsDiffUtilCallback : DiffUtil.ItemCallback<TripModel>(){
    override fun areItemsTheSame(oldItem: TripModel, newItem: TripModel): Boolean {
        return oldItem.trip_id == newItem.trip_id
    }

    override fun areContentsTheSame(oldItem: TripModel, newItem: TripModel): Boolean {
        return oldItem.trip_id == newItem.trip_id &&
                oldItem.trip_title == newItem.trip_title &&
                oldItem.trip_desc == newItem.trip_desc &&
                oldItem.place_from == newItem.place_from &&
                oldItem.place_to == newItem.place_to &&
                oldItem.journey_date == newItem.journey_date &&
                oldItem.return_date == newItem.return_date &&
                oldItem.user_id == newItem.user_id &&
                oldItem.date_created == newItem.date_created &&
                oldItem.duration_in_days == newItem.duration_in_days &&
                oldItem.total_heads == newItem.total_heads
    }

}