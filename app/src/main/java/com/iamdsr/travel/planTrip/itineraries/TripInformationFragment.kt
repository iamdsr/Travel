package com.iamdsr.travel.planTrip.itineraries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.R
import com.iamdsr.travel.databinding.FragmentPlanTripBinding
import com.iamdsr.travel.databinding.FragmentTripInformationBinding
import com.iamdsr.travel.models.TripModel


class TripInformationFragment : Fragment() {

    // Utils
    private var tripModel = TripModel()
    private lateinit var binding: FragmentTripInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_trip_information, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mySharedPreferences = MySharedPreferences(requireContext())
        tripModel = mySharedPreferences.getTripModel()
        setValueToTextViews()
    }

    private fun setValueToTextViews() {
        binding.tripTitle.text = tripModel.trip_title
        binding.tripDesc.text = tripModel.trip_desc
        binding.totalDuration.text = tripModel.duration_in_days.toString()
        binding.journeyDate.text = tripModel.journey_date
        binding.startDate.text = tripModel.journey_date
        binding.journeyMode.text = tripModel.journey_mode
        when (tripModel.journey_mode){
            "Car" -> binding.journeyMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_car_journey, 0, 0, 0)
            "Train" -> binding.journeyMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_train_journey, 0, 0, 0)
            "Flight" -> binding.journeyMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_flight_journey, 0, 0, 0)
        }
        binding.returnDate.text = tripModel.return_date
        binding.placeFrom.text = tripModel.place_from
        binding.placeTo.text = tripModel.place_to
        binding.totalHeads.text = tripModel.total_heads.toString()
    }
}