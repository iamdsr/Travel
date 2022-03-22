package com.iamdsr.travel.planTrip.itineraries

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.ItineraryRecyclerAdapter
import com.iamdsr.travel.customRecyclerViewAdapters.PlannedTripRecyclerAdapter
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.viewModels.ItinerarySharedViewModel
import com.iamdsr.travel.viewModels.ItineraryTimelineViewModel
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel
import java.time.Duration


class TimelineFragment : Fragment() {

    // Widgets
    private lateinit var mTitle: TextView
    private var mItineraryRecyclerView: RecyclerView? = null

    // Utils
    private var tripID: String = ""
    private lateinit var tripTitle: String
    private lateinit var itineraryList: List<ItineraryModel>
    private lateinit var itineraryRecyclerAdapter: ItineraryRecyclerAdapter

    private lateinit var addNewItinerary: ExtendedFloatingActionButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        initRecyclerView()
        addNewItinerary.setOnClickListener(View.OnClickListener {
            setupDialog()
        })
        val itinerarySharedViewModel = ViewModelProvider(requireActivity())[ItinerarySharedViewModel::class.java]
        itinerarySharedViewModel.getModel().observe(requireActivity(), Observer {
            tripTitle = it.trip_title
            mTitle.text = tripTitle
            tripID = it.trip_id
        })
        val itineraryTimelineViewModel = ViewModelProvider(requireActivity())[ItineraryTimelineViewModel::class.java]
        itineraryTimelineViewModel._getAllSavedItinerariesFromFirebaseFirestore(tripID)?.observe(this, Observer { it->
            itineraryList = it
            itineraryRecyclerAdapter.submitList(itineraryList)
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

    private fun initRecyclerView() {
        mItineraryRecyclerView?.layoutManager = LinearLayoutManager(context)
        mItineraryRecyclerView?.setHasFixedSize(true)
        itineraryRecyclerAdapter = ItineraryRecyclerAdapter()
        mItineraryRecyclerView?.adapter = itineraryRecyclerAdapter
    }

    private fun setupWidgets() {

        if (view != null){
            addNewItinerary = view!!.findViewById(R.id.add_new_itinerary)
            mTitle =  view!!.findViewById(R.id.trip_title)
            mItineraryRecyclerView = view!!.findViewById(R.id.itinerary_recycler_view)
        }
    }
}