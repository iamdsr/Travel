package com.iamdsr.travel.planTrip.itineraries

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.iamdsr.travel.R
import com.iamdsr.travel.customRecyclerViewAdapters.ItineraryRecyclerAdapter
import com.iamdsr.travel.interfaces.RecyclerViewActionsInterface
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.utils.AppConstants
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.viewModels.ItineraryTimelineViewModel
import java.util.*


class TimelineFragment : Fragment(), RecyclerViewActionsInterface {

    // Widgets
    private lateinit var mTitle: TextView
    private var mItineraryRecyclerView: RecyclerView? = null
    private lateinit var mRootLayout: CoordinatorLayout

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
            itineraryRecyclerAdapter.submitList(itineraryList)
        })
    }

    private fun setupDialog() {
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setTitle("Select Itinerary Type")

        val animals = arrayOf("Travel to Destination", "Hotel Check-In", "Roam nearby/ Sightseeing ")
        builder.setItems(animals) { _, which ->
            when (which) {
                0 -> {
                    findNavController().navigate(R.id.action_myItinerariesFragment_to_journeyFragment)
                }
                1 -> {
                    findNavController().navigate(R.id.action_myItinerariesFragment_to_addHotelCheckInFragment)
                }
                2 -> {
                    findNavController().navigate(R.id.action_myItinerariesFragment_to_addSightseeingFragment)
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mItineraryRecyclerView?.layoutManager = linearLayoutManager
        mItineraryRecyclerView?.setHasFixedSize(true)
        itineraryRecyclerAdapter = ItineraryRecyclerAdapter(this)
        mItineraryRecyclerView?.adapter = itineraryRecyclerAdapter
    }

    private fun setupWidgets() {

        if (view != null){
            addNewItinerary = view!!.findViewById(R.id.add_new_itinerary)
            mTitle =  view!!.findViewById(R.id.trip_title)
            mItineraryRecyclerView = view!!.findViewById(R.id.itinerary_recycler_view)
            mRootLayout = view!!.findViewById(R.id.layout)
        }
    }

    private fun setUpWarningDialogToComplete(itineraryItem: ItineraryModel) {
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.layout_dialog_confirm_cancel, null)
        val mDialogTitle  = dialogView.findViewById<View>(R.id.title) as TextView
        val mDialogDesc  = dialogView.findViewById<View>(R.id.desc) as TextView
        val mDialogCancelBtn  = dialogView.findViewById<View>(R.id.cancel) as Button
        val mDialogConfirmBtn  = dialogView.findViewById<View>(R.id.confirm) as Button

        mDialogTitle.setText(R.string.mark_trip_as_complete)
        mDialogDesc.setText(R.string.mark_complete_message)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        mDialogCancelBtn.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        mDialogConfirmBtn.setOnClickListener(View.OnClickListener {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            val itineraryTimelineViewModel = ViewModelProvider(this)[ItineraryTimelineViewModel::class.java]
            itineraryMap["completed"] = true
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryItem)
            dialog.dismiss()
        })
    }
    private fun displaySnackBar(){
        Snackbar.make(mRootLayout, "This item has been already marked as completed", Snackbar.LENGTH_LONG).show()
    }
    override fun onItemClick(view: View, position: Int) {
        val itineraryItem = itineraryList[position]

        if (view.resources.getResourceName(view.id) == "com.iamdsr.travel:id/completed"){
            if (!itineraryItem.completed){
                setUpWarningDialogToComplete(itineraryItem)
            }
            else {
                Log.d("TAG", "onItemClick: 1 ")
                displaySnackBar()
            }
        }
        else if (view.resources.getResourceName(view.id) == "com.iamdsr.travel:id/edit" && !itineraryItem.completed){
            when (itineraryItem.type) {
                AppConstants.HOTEL_CHECK_IN_ITINERARY_TYPE -> {
                    val bundle = Bundle()
                    bundle.putSerializable("OBJ", itineraryItem)
                    findNavController().navigate(R.id.action_global_updateHotelCheckInFragment, bundle)
                }
                AppConstants.JOURNEY_ITINERARY_TYPE -> {
                    val bundle = Bundle()
                    bundle.putSerializable("OBJ", itineraryItem)
                    findNavController().navigate(R.id.action_global_updateJourneyFragment, bundle)
                }
                AppConstants.SIGHT_SEEING_ITINERARY_TYPE -> {
                    val bundle = Bundle()
                    bundle.putSerializable("OBJ", itineraryItem)
                    findNavController().navigate(R.id.action_global_updateSightseeingFragment, bundle)
                }
            }
        }
        else {
            Log.d("TAG", "onItemClick: 2 ")
            displaySnackBar()
        }
    }
}