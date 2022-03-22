package com.iamdsr.travel.planTrip.itineraries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.R
import com.iamdsr.travel.models.TripModel


class TripInformationFragment : Fragment() {

    // Utils
    private var tripModel = TripModel()

    // Widgets
    private lateinit var mTitle: TextView
    private lateinit var mDesc: TextView
    private lateinit var mDuration: TextView
    private lateinit var mStartDate: TextView
    private lateinit var mJourneyMode: TextView
    private lateinit var mJourneyDate: TextView
    private lateinit var mReturnDate: TextView
    private lateinit var mWhereFrom: TextView
    private lateinit var mWhereTo: TextView
    private lateinit var mNumberOfPerson: TextView
    private lateinit var mProgress: ProgressBar


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        mProgress.visibility = View.VISIBLE
        val mySharedPreferences = MySharedPreferences(context!!)
        tripModel = mySharedPreferences.getTripModel()
        setValueToTextViews()
    }

    private fun setValueToTextViews() {
        mTitle.text = tripModel.trip_title
        mDesc.text = tripModel.trip_desc
        mDuration.text = tripModel.duration_in_days.toString()
        mStartDate.text = tripModel.journey_date
        mJourneyMode.text = tripModel.journey_mode
        when (tripModel.journey_mode){
            "Car" -> mJourneyMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_car_journey, 0, 0, 0)
            "Train" -> mJourneyMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_train_journey, 0, 0, 0)
            "Flight" -> mJourneyMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flight_journey, 0, 0, 0)
        }
        mJourneyDate.text = tripModel.journey_date
        mReturnDate.text = tripModel.return_date
        mWhereFrom.text = tripModel.place_from
        mWhereTo.text = tripModel.place_to
        mNumberOfPerson.text = tripModel.total_heads.toString()
        mProgress.visibility = View.INVISIBLE
    }

    private fun setupWidgets() {
        if (view != null){
            mProgress = view!!.findViewById(R.id.progress_horizontal)
            mTitle = view!!.findViewById(R.id.trip_title)
            mDesc = view!!.findViewById(R.id.trip_desc)
            mDuration = view!!.findViewById(R.id.total_duration)
            mStartDate = view!!.findViewById(R.id.start_date)
            mJourneyMode = view!!.findViewById(R.id.journey_mode)
            mNumberOfPerson = view!!.findViewById(R.id.total_heads)
            mReturnDate = view!!.findViewById(R.id.return_date)
            mJourneyDate = view!!.findViewById(R.id.journey_date)
            mWhereFrom = view!!.findViewById(R.id.place_from)
            mWhereTo = view!!.findViewById(R.id.place_to)
        }
    }
}