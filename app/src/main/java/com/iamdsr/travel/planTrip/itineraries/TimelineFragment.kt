package com.iamdsr.travel.planTrip.itineraries

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.ItineraryRecyclerAdapter
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.viewModels.ItineraryTimelineViewModel
import java.util.*


class TimelineFragment : Fragment() {

    // Widgets
    private lateinit var mTitle: TextView
    private var mItineraryRecyclerView: RecyclerView? = null

    // Utils
    private lateinit var tripID: String
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
        val mySharedPreferences = MySharedPreferences(context!!)
        tripID = mySharedPreferences.getTripModel().trip_id
        tripTitle = mySharedPreferences.getTripModel().trip_title
        mTitle.text = tripTitle
        initRecyclerView()
        addNewItinerary.setOnClickListener(View.OnClickListener {
            setupDialog()
        })
        val itineraryTimelineViewModel = ViewModelProvider(requireActivity())[ItineraryTimelineViewModel::class.java]
        itineraryTimelineViewModel._getAllSavedItinerariesFromFirebaseFirestore(tripID).observe(this, Observer {
            itineraryList = it
            //Collections.reverse(itineraryList)
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
                1 -> {
                    val bundle = Bundle()
                    bundle.putLong("LIST_SIZE", itineraryList.size.toLong())
                    findNavController().navigate(R.id.action_myItinerariesFragment_to_addHotelCheckInFragment, bundle)
                }
                2 -> { findNavController().navigate(R.id.action_myItinerariesFragment_to_addSightseeingFragment) }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        //linearLayoutManager.reverseLayout = true;
        //linearLayoutManager.stackFromEnd = true;
        mItineraryRecyclerView?.layoutManager = linearLayoutManager
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