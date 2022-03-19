package com.iamdsr.travel.planTrip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.PlannedTripRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel

class PlanTripFragment : Fragment(), RecyclerViewActionsInterface {

    // Widgets
    private var myView: View?= null
    private var mAddTrip: ExtendedFloatingActionButton?= null
    private var mPlannedTripRecyclerView: RecyclerView? = null
    private lateinit var tripList: List<TripModel>

    // Utils
    private lateinit var plannedTripsRecyclerAdapter: PlannedTripRecyclerAdapter
//    private var planList: ArrayList<TripModel> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.fragment_plan_trip, container, false)
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWidgets()
        initRecyclerView()
        val planTripFragmentViewModel = ViewModelProvider(requireActivity())[PlanTripFragmentViewModel::class.java]
        planTripFragmentViewModel._getAllSavedTripFromFirebaseFirestore().observe(this, Observer { it->
            tripList = it
            plannedTripsRecyclerAdapter.submitList(it)
        })
        mAddTrip?.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_planTripFragment_to_addTripFragment)
        })
    }

    private fun initRecyclerView() {
        mPlannedTripRecyclerView?.layoutManager = LinearLayoutManager(context)
        mPlannedTripRecyclerView?.setHasFixedSize(true)
        plannedTripsRecyclerAdapter = PlannedTripRecyclerAdapter(this)
        mPlannedTripRecyclerView?.adapter = plannedTripsRecyclerAdapter
    }

    private fun setUpWidgets() {
        mAddTrip = myView?.findViewById(R.id.add_new_trip)
        mPlannedTripRecyclerView = myView?.findViewById(R.id.trip_recycler_view)
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.resources.getResourceName(view.id) == "com.iamdsr.travel:id/update"){
            val bundle: Bundle = Bundle()
            bundle.putString("TRIP_ID",  tripList[position].trip_id)
            bundle.putString("TRIP_TITLE",  tripList[position].trip_title)
            bundle.putString("TRIP_DESCRIPTION",tripList[position].trip_desc)
            bundle.putString("JOURNEY_DATE",  tripList[position].journey_date)
            bundle.putString("RETURN_DATE",  tripList[position].return_date)
            bundle.putString("PLACE_FROM", tripList[position].place_from)
            bundle.putString("PLACE_TO", tripList[position].place_to)
            bundle.putLong("TOTAL_PAX", tripList[position].total_heads)
            bundle.putString("JOURNEY_MODE", tripList[position].journey_mode)
            findNavController().navigate(R.id.action_planTripFragment_to_updateTripFragment, bundle)
        }
        else if (view.resources.getResourceName(view.id) == "com.iamdsr.travel:id/addItinerary"){
            val bundle: Bundle = Bundle()
            bundle.putString("TRIP_ID",  tripList[position].trip_id)
            bundle.putString("TRIP_TITLE",  tripList[position].trip_title)
            bundle.putString("TRIP_DESCRIPTION",tripList[position].trip_desc)
            bundle.putString("JOURNEY_DATE",  tripList[position].journey_date)
            bundle.putString("RETURN_DATE",  tripList[position].return_date)
            bundle.putString("PLACE_FROM", tripList[position].place_from)
            bundle.putString("PLACE_TO", tripList[position].place_to)
            bundle.putLong("TOTAL_PAX", tripList[position].total_heads)
            bundle.putString("JOURNEY_MODE", tripList[position].journey_mode)
            findNavController().navigate(R.id.action_planTripFragment_to_myItinerariesFragment, bundle)
        }
    }
}