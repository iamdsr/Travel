package com.iamdsr.travel.planTrip.itineraries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.R
import com.iamdsr.travel.models.TripModel

class MyItinerariesFragment : Fragment() {

    //Widgets
    lateinit var itineraryViewPager : ViewPager2
    lateinit var itineraryTabLayout: TabLayout

    // Utils
    private var titleBundle: String = ""
    private var descBundle: String = ""
    private var jDateBundle: String = ""
    private var rDateBundle: String = ""
    private var fromBundle: String = ""
    private var toBundle: String = ""
    private var totalHeadsBundle: Long = -1
    private var totalDurationBundle: Long = -1
    private var tripIDBundle: String = ""
    private var journeyModeBundle: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_itineraries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            tripIDBundle = arguments!!.getString("TRIP_ID","")
            titleBundle = arguments!!.getString("TRIP_TITLE","")
            descBundle = arguments!!.getString("TRIP_DESCRIPTION","")
            jDateBundle = arguments!!.getString("JOURNEY_DATE","")
            rDateBundle = arguments!!.getString("RETURN_DATE","")
            fromBundle = arguments!!.getString("PLACE_FROM","")
            toBundle = arguments!!.getString("PLACE_TO","")
            totalHeadsBundle = arguments!!.getLong("TOTAL_PAX",-1)
            totalDurationBundle = arguments!!.getLong("DURATION",-1)
            journeyModeBundle = arguments!!.getString("JOURNEY_MODE","")
        }

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (itineraryViewPager.currentItem == 1){
                        itineraryViewPager.currentItem = 0
                    }
                    else{
                        findNavController().navigateUp()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        setupWidgets()
        setUpViewPager()
        val mySharedPreferences = MySharedPreferences(context!!)
        val tripModel = TripModel(
            tripIDBundle,
            titleBundle,
            descBundle,
            jDateBundle,
            rDateBundle,
            fromBundle,
            toBundle,
            FirebaseAuth.getInstance().currentUser!!.uid,
            "",
            journeyModeBundle,
            totalDurationBundle,
            totalHeadsBundle
        )
        mySharedPreferences.setTripModel(tripModel)
    }


    private fun setUpViewPager(){
        val itineraryViewPagerAdapter = ItineraryViewPagerAdapter(requireActivity().supportFragmentManager,requireActivity().lifecycle)
        itineraryViewPager.adapter = itineraryViewPagerAdapter

        TabLayoutMediator(itineraryTabLayout, itineraryViewPager){tab, position ->

            when (position){
                0 -> {
                    tab.text = "Trip Info"
                }
                1 -> {
                    tab.text = "Timeline"
                }
            }
        }.attach()
    }

    private fun setupWidgets() {
        itineraryTabLayout = view!!.findViewById(R.id.itinerary_tab_layout)
        itineraryViewPager = view!!.findViewById(R.id.itinerary_view_pager)
    }
}