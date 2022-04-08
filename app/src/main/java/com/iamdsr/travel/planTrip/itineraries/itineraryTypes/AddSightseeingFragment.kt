package com.iamdsr.travel.planTrip.itineraries.itineraryTypes

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.repositories.FirestoreRepository
import com.iamdsr.travel.utils.AppConstants
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.viewModels.ItineraryTimelineViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class AddSightseeingFragment : Fragment() {

    // Widgets
    private lateinit var mTitle: TextInputEditText
    private lateinit var mDesc: TextInputEditText
    private lateinit var mVisitDate: TextInputEditText
    private lateinit var mVisitTime: TextInputEditText
    private lateinit var mPlaceName: TextInputEditText
    private lateinit var mPlaceAddress: TextInputEditText
    private lateinit var mDayOfTrip: TextInputEditText
    private lateinit var mAddItineraryButton: Button

    private lateinit var mTitleContainer: TextInputLayout
    private lateinit var mDescContainer: TextInputLayout
    private lateinit var mVisitDateContainer: TextInputLayout
    private lateinit var mVisitTimeContainer: TextInputLayout
    private lateinit var mSightNameContainer: TextInputLayout
    private lateinit var mSightAddressContainer: TextInputLayout
    private lateinit var mDayOfTripContainer: TextInputLayout

    private lateinit var snack_layout: CoordinatorLayout

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()
    private var tripID: String=""
    private var tripTitle: String=""
    private var firebaseRepository = FirestoreRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_sightseeing, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()

        val mySharedPreferences = MySharedPreferences(context!!)
        tripID = mySharedPreferences.getTripModel().trip_id
        tripTitle = mySharedPreferences.getTripModel().trip_title

        mVisitTime.setOnClickListener(View.OnClickListener {
            setUpTimeDialogs()
        })
        mVisitDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })
        mAddItineraryButton.setOnClickListener(View.OnClickListener {
            addNewItinerary()
        })
    }

    private fun setUpTimeDialogs(){
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Select Hotel Check-In Time")
            .setHour(12)
            .setMinute(10)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()
        materialTimePicker.show(requireActivity().supportFragmentManager,null)
        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute
            myCalendar!![Calendar.HOUR_OF_DAY] = pickedHour
            myCalendar[Calendar.MINUTE] = pickedMinute
            val formattedTime: String = when {
                pickedHour > 12 -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour - 12}:0${materialTimePicker.minute} pm"
                    } else {
                        "${materialTimePicker.hour - 12}:${materialTimePicker.minute} pm"
                    }
                }
                pickedHour == 12 -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour}:0${materialTimePicker.minute} pm"
                    } else {
                        "${materialTimePicker.hour}:${materialTimePicker.minute} pm"
                    }
                }
                pickedHour == 0 -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour + 12}:0${materialTimePicker.minute} am"
                    } else {
                        "${materialTimePicker.hour + 12}:${materialTimePicker.minute} am"
                    }
                }
                else -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour}:0${materialTimePicker.minute} am"
                    } else {
                        "${materialTimePicker.hour}:${materialTimePicker.minute} am"
                    }
                }
            }
            mVisitTime.setText(formattedTime)
        }
    }
    private fun setUpDateDialogs(view: TextInputEditText)  {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTheme(R.style.ThemeOverlay_App_DatePicker).build()
        datePicker.show(parentFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            val date = dateFormatter.format(Date(it))
            view.setText(date)
        }
        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(context, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
        }
        datePicker.addOnCancelListener {
            Toast.makeText(context, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkDataOfInputFields(title: String, desc: String, visitDate: String, visitTime: String, sightName: String, sightAddress: String, day: String): Boolean{
        var errorFound = false
        if (title.length > 50){
            errorFound = true
            mTitleContainer.error = context!!.getString(R.string.length_exceeds_error, 50)
        }
        else {
            mTitleContainer.error = null
        }
        if (desc.length > 200){
            errorFound = true
            mDescContainer.error = context!!.getString(R.string.length_exceeds_error, 200)
        }
        else {
            mDescContainer.error = null
        }
        if (sightName.length > 30){
            errorFound = true
            mSightNameContainer.error = context!!.getString(R.string.length_exceeds_error, 30)
        }
        else {
            mSightNameContainer.error = null
        }
        if (sightAddress.length > 50){
            errorFound = true
            mSightAddressContainer.error = context!!.getString(R.string.length_exceeds_error, 50)
        }
        else {
            mSightAddressContainer.error = null
        }
        if (day.toInt() < 0){
            errorFound = true
            mDayOfTripContainer.error = context!!.getString(R.string.invalid_value_error, day)
        }
        else {
            mDayOfTripContainer.error = null
        }
        return errorFound
    }
    private fun addNewItinerary(){
        val title: String = mTitle.text.toString().trim()
        val desc: String = mDesc.text.toString().trim()
        val visitDate: String = mVisitDate.text.toString().trim()
        val visitTime: String = mVisitTime.text.toString().trim()
        val placeName: String = mPlaceName.text.toString().trim()
        val placeAddress: String = mPlaceAddress.text.toString().trim()
        val dayOfTrip: String = mDayOfTrip.text.toString().trim()

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(visitDate) && !TextUtils.isEmpty(visitTime) &&
            !TextUtils.isEmpty(placeName) && !TextUtils.isEmpty(placeAddress) && !TextUtils.isEmpty(dayOfTrip)) {

            val error = checkDataOfInputFields(title, desc, visitDate, visitTime, placeName, placeAddress, dayOfTrip)
            Log.d("TAG", "addNewTrip: Error $error")
            if (!error){
                val timeObj = parseStringDateTime("$visitDate $visitTime")
                val itineraryModel = ItineraryModel(
                    firebaseRepository.getNewItineraryID(tripID),
                    title,
                    desc,
                    visitDate,
                    visitTime,
                    AppConstants.SIGHT_SEEING_ITINERARY_TYPE,
                    "",
                    "",
                    "",
                    "",
                    "",
                    placeName,
                    placeAddress,
                    getTimestamp(),
                    tripID,
                    tripTitle,
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    false,
                    dayOfTrip.toLong(),
                    timeObj
                )
                Log.d("TAG", "addNewItinerary: Itinerary Model : $itineraryModel")
                val itineraryTimelineViewModel = ViewModelProvider(this)[ItineraryTimelineViewModel::class.java]
                itineraryTimelineViewModel._addNewItineraryToFirebaseFirestore(itineraryModel)
                Toast.makeText(context,"Itinerary added successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
        else {
            Snackbar.make(snack_layout, context!!.resources.getString(R.string.empty_fields_msg), Snackbar.LENGTH_LONG).show()
        }
    }
    private fun parseStringDateTime(stringDateTime: String): Date {
        val pattern = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a")
        val localDateTime = LocalDateTime.parse(stringDateTime, pattern)
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

    private fun getTimestamp(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getDefault()
        return format.format(Date())
    }
    private fun setupWidgets() {
        if (view != null){
            mTitle = view!!.findViewById(R.id.title)
            mDesc = view!!.findViewById(R.id.desc)
            mVisitDate = view!!.findViewById(R.id.date)
            mVisitTime = view!!.findViewById(R.id.time)
            mPlaceName = view!!.findViewById(R.id.place_name)
            mPlaceAddress = view!!.findViewById(R.id.place_address)
            mAddItineraryButton = view!!.findViewById(R.id.add_new_itinerary_btn)
            mDayOfTrip = view!!.findViewById(R.id.day)
            snack_layout = view!!.findViewById(R.id.snackbar_layout)
            mTitleContainer = view!!.findViewById(R.id.title_container)
            mDescContainer = view!!.findViewById(R.id.desc_container)
            mVisitDateContainer = view!!.findViewById(R.id.date_container)
            mVisitTimeContainer = view!!.findViewById(R.id.time_container)
            mSightNameContainer = view!!.findViewById(R.id.place_name_container)
            mSightAddressContainer = view!!.findViewById(R.id.place_address_container)
            mDayOfTripContainer = view!!.findViewById(R.id.day_container)
        }
    }
}