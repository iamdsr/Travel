package com.iamdsr.travel.planTrip.itineraries

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.iamdsr.travel.R
import com.iamdsr.travel.viewModels.ItinerarySharedViewModel


class TripInformationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itinerarySharedViewModel = ViewModelProvider(requireActivity())[ItinerarySharedViewModel::class.java]
        Log.d("TAG", "before observe: Trip id : ${itinerarySharedViewModel.getText().value}" )
        itinerarySharedViewModel.getText().observe(requireActivity(), Observer {
            Log.d("TAG", "onViewCreated: $it")
        })
    }
}