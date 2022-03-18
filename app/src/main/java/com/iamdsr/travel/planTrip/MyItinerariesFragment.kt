package com.iamdsr.travel.planTrip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.iamdsr.travel.R

class MyItinerariesFragment : Fragment() {

    //Widgets
    lateinit var itineraryViewPager : ViewPager2
    lateinit var itineraryTabLayout: TabLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_itineraries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setUpViewPager()

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