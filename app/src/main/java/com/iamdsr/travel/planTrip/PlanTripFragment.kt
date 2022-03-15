package com.iamdsr.travel.planTrip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        //createDummyData()
        val planTripFragmentViewModel = ViewModelProvider(this)[PlanTripFragmentViewModel::class.java]
        planTripFragmentViewModel.getSavedTrips().observe(this, Observer { it->
            tripList = it
            plannedTripsRecyclerAdapter.submitList(it)
        })
        mAddTrip?.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_planTripFragment_to_addTripFragment)
        })
    }

//    private fun createDummyData() {
//        for (i in 1..10){
//            planList.add(TripModel("i","Title $i","Desc $i","02/03/2022","10/03/2022","Kolkata","Delhi","${i+10}","10/03/2022",12, 5))
//        }
//        plannedTripsRecyclerAdapter.submitList(planList)
//    }

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

    override fun onItemClick(position: Int) {
        Toast.makeText(context, tripList[position].trip_title, Toast.LENGTH_LONG).show()
    }
}