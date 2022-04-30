package com.iamdsr.travel.planTrip.itineraries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.R
import com.iamdsr.travel.databinding.FragmentMyItinerariesBinding
import com.iamdsr.travel.databinding.FragmentUpdateTripBinding
import com.iamdsr.travel.models.TripModel

class MyItinerariesFragment : Fragment() {

    // Utils
    private lateinit var tripModelBundle: TripModel
    private lateinit var binding: FragmentMyItinerariesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_itineraries, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            tripModelBundle = requireArguments().getSerializable("TRIP_MODEL") as TripModel
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.itineraryViewPager.currentItem == 1){
                        binding.itineraryViewPager.currentItem = 0
                    }
                    else{
                        findNavController().navigateUp()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        setUpViewPager()
        val mySharedPreferences = MySharedPreferences(requireContext())
        val tripModel = TripModel(
            tripModelBundle.trip_id,
            tripModelBundle.trip_title,
            tripModelBundle.trip_desc,
            tripModelBundle.journey_date,
            tripModelBundle.return_date,
            tripModelBundle.place_from,
            tripModelBundle.place_to,
            FirebaseAuth.getInstance().currentUser!!.uid,
            "",
            tripModelBundle.journey_mode,
            tripModelBundle.duration_in_days,
            tripModelBundle.total_heads
        )
        mySharedPreferences.setTripModel(tripModel)
    }


    private fun setUpViewPager(){
        val itineraryViewPagerAdapter = ItineraryViewPagerAdapter(requireActivity().supportFragmentManager,requireActivity().lifecycle)
        binding.itineraryViewPager.adapter = itineraryViewPagerAdapter

        TabLayoutMediator(binding.itineraryTabLayout, binding.itineraryViewPager){tab, position ->

            when (position){
                0 -> {
                    tab.text = "Trip Info"
                }
                1 -> {
                    tab.text = "Timeline"
                }
            }
        }.attach()
        binding.itineraryViewPager.offscreenPageLimit = 2
    }
}