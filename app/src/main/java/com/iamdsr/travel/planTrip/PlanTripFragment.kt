package com.iamdsr.travel.planTrip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.PlannedTripRecyclerAdapter
import com.iamdsr.travel.databinding.FragmentPlanTripBinding
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel

class PlanTripFragment : Fragment(), RecyclerViewActionsInterface {

    // Utils
    private lateinit var plannedTripsRecyclerAdapter: PlannedTripRecyclerAdapter
    private lateinit var tripList: List<TripModel>
    private lateinit var binding: FragmentPlanTripBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_plan_trip, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        val planTripFragmentViewModel = ViewModelProvider(requireActivity())[PlanTripFragmentViewModel::class.java]
        planTripFragmentViewModel._getAllSavedTripFromFirebaseFirestore().observe(requireActivity()) {
            tripList = it
            plannedTripsRecyclerAdapter.submitList(it)
        }
        binding.addNewTrip.setOnClickListener {
            findNavController().navigate(R.id.action_planTripFragment_to_addTripFragment)
        }
    }

    private fun initRecyclerView() {
        binding.tripRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.tripRecyclerView.setHasFixedSize(true)
        plannedTripsRecyclerAdapter = PlannedTripRecyclerAdapter(this)
        binding.tripRecyclerView.adapter = plannedTripsRecyclerAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.resources.getResourceName(view.id) == "com.iamdsr.travel:id/update"){
            val bundle = Bundle()
            bundle.putSerializable("TRIP_MODEL",  tripList[position])
            findNavController().navigate(R.id.action_planTripFragment_to_updateTripFragment, bundle)
        }
        else if (view.resources.getResourceName(view.id) == "com.iamdsr.travel:id/addItinerary"){
            val bundle = Bundle()
            bundle.putSerializable("TRIP_MODEL",  tripList[position])
            findNavController().navigate(R.id.action_planTripFragment_to_myItinerariesFragment, bundle)
        }
    }
}