package com.iamdsr.travel.planTrip.itineraries

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.iamdsr.travel.R
import java.time.Duration


class TimelineFragment : Fragment() {

    private lateinit var addNewItinerary: ExtendedFloatingActionButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        addNewItinerary.setOnClickListener(View.OnClickListener {
            setupDialog()
        })
    }

    private fun setupDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select Itinerary Type")

        val animals = arrayOf("Travel to Destination", "Hotel Check-In", "Roam nearby/ Sightseeing ")
        builder.setItems(animals) { _, which ->
            when (which) {
                0 -> { findNavController().navigate(R.id.action_myItinerariesFragment_to_journeyFragment) }
                1 -> { findNavController().navigate(R.id.action_myItinerariesFragment_to_addHotelCheckInFragment) }
                2 -> { findNavController().navigate(R.id.action_myItinerariesFragment_to_addSightseeingFragment) }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setupWidgets() {

        if (view != null){
            addNewItinerary = view!!.findViewById(R.id.add_new_itinerary)
        }
    }
}