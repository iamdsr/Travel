package com.iamdsr.travel.planTrip.itineraries

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iamdsr.travel.R
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.viewModels.ItinerarySharedViewModel


class TripInformationFragment : Fragment() {

    // Utils
    private var tripID: String=""
    private var model = TripModel()

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
        val itinerarySharedViewModel = ViewModelProvider(requireActivity())[ItinerarySharedViewModel::class.java]
        itinerarySharedViewModel.getText().observe(requireActivity(), Observer {
            tripID = it
        })
        itinerarySharedViewModel._getSingleTripFromFirestoreDatabase(tripID).observe(requireActivity(), Observer {
            model = it
            if (model!=null){
                mTitle.text = model.trip_title
                mDesc.text = model.trip_desc
                mDuration.text = model.duration_in_days.toString()
                mStartDate.text = model.journey_date
                mJourneyMode.text = model.journey_mode
                mJourneyDate.text = model.journey_date
                mReturnDate.text = model.return_date
                mWhereFrom.text = model.place_from
                mWhereTo.text = model.place_to
                mNumberOfPerson.text = model.total_heads.toString()
                mProgress.visibility = View.INVISIBLE
            }
        })
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