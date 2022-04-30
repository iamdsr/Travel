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
import com.iamdsr.travel.repositories.PlanTripsFirestoreRepository
import com.iamdsr.travel.utils.MySharedPreferences
import com.iamdsr.travel.viewModels.ItineraryTimelineViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class JourneyFragment : Fragment() {

    // Widgets
    private lateinit var mTitle: TextInputEditText
    private lateinit var mDesc: TextInputEditText
    private lateinit var mStartDate: TextInputEditText
    private lateinit var mStartTime: TextInputEditText
    private lateinit var mPlaceFrom: TextInputEditText
    private lateinit var mPlaceTo: TextInputEditText
    private lateinit var mDayOfTrip: TextInputEditText
    private lateinit var mJourneyMode: AutoCompleteTextView
    private lateinit var mAddItineraryButton: Button

    private lateinit var mTitleContainer: TextInputLayout
    private lateinit var mDescContainer: TextInputLayout
    private lateinit var mStartDateContainer: TextInputLayout
    private lateinit var mStartTimeContainer: TextInputLayout
    private lateinit var mPlaceFromContainer: TextInputLayout
    private lateinit var mPlaceToContainer: TextInputLayout
    private lateinit var mJourneyModeContainer: TextInputLayout
    private lateinit var mDayOfTripContainer: TextInputLayout

    private lateinit var snack_layout: CoordinatorLayout

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()
    private var tripID: String=""
    private var tripTitle: String=""
    private val journeyModeEntries = arrayOf("Flight", "Train", "Car")
    private var firebaseRepository = PlanTripsFirestoreRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()

        mStartDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })
        val mySharedPreferences = MySharedPreferences(context!!)
        tripID = mySharedPreferences.getTripModel().trip_id
        tripTitle = mySharedPreferences.getTripModel().trip_title
        mStartTime.setOnClickListener(View.OnClickListener {
            setUpTimeDialogs()
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
            mStartTime.setText(formattedTime)
        }
    }
    private fun setUpDateDialogs(view: TextInputEditText) {
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

    private fun addNewItinerary(){
        val title: String = mTitle.text.toString().trim()
        val desc: String = mDesc.text.toString().trim()
        val startDate: String = mStartDate.text.toString().trim()
        val startTime: String = mStartTime.text.toString().trim()
        val placeFrom: String = mPlaceFrom.text.toString().trim()
        val placeTo: String = mPlaceTo.text.toString().trim()
        val journeyMode: String = mJourneyMode.text.toString().trim()
        val dayOfTrip: String = mDayOfTrip.text.toString().trim()

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(startTime) &&
            !TextUtils.isEmpty(placeFrom) && !TextUtils.isEmpty(placeTo) && !TextUtils.isEmpty(journeyMode) && !TextUtils.isEmpty(dayOfTrip)) {

            val error = checkDataOfInputFields(title, desc, startDate, startTime, placeFrom, placeTo, journeyMode, dayOfTrip)
            Log.d("TAG", "addNewTrip: Error:$error-$journeyMode")

            if (!error){

                val timeObj = parseStringDateTime("$startDate $startTime")

                val itineraryModel = ItineraryModel(
                    firebaseRepository.getNewItineraryID(tripID),
                    title,
                    desc,
                    startDate,
                    startTime,
                    "JOURNEY",
                    journeyMode,
                    placeFrom,
                    placeTo,
                    "",
                    "",
                    "",
                    "",
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
    private fun checkDataOfInputFields(title: String, desc: String, startDate: String, startTime: String, placeFrom: String, placeTo: String, jMode: String, day: String): Boolean{
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
        if (placeFrom.length > 20){
            errorFound = true
            mPlaceFromContainer.error = context!!.getString(R.string.length_exceeds_error, 20)
        }
        else {
            mPlaceFromContainer.error = null
        }
        if (placeTo.length > 20){
            errorFound = true
            mPlaceToContainer.error = context!!.getString(R.string.length_exceeds_error, 20)
        }
        else {
            mPlaceToContainer.error = null
        }
        if (!journeyModeEntries.contains(jMode)){
            Log.d("TAG", "checkDataOfInputFields: $jMode")
            errorFound = true
            mJourneyModeContainer.error = context!!.getString(R.string.invalid_value_error, jMode)
        }
        else {
            mJourneyModeContainer.error = null
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

    private fun getTimestamp(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getDefault()
        return format.format(Date())
    }
    private fun setupWidgets() {
        if (view != null){
            mTitle = view!!.findViewById(R.id.title)
            mDesc = view!!.findViewById(R.id.desc)
            mStartDate = view!!.findViewById(R.id.start_date)
            mStartTime = view!!.findViewById(R.id.start_time)
            mPlaceFrom = view!!.findViewById(R.id.place_from)
            mPlaceTo = view!!.findViewById(R.id.place_to)
            mJourneyMode = view!!.findViewById(R.id.journey_mode)
            mAddItineraryButton = view!!.findViewById(R.id.add_new_itinerary_btn)
            mDayOfTrip = view!!.findViewById(R.id.day)
            snack_layout = view!!.findViewById(R.id.snackbar_layout)
            mTitleContainer = view!!.findViewById(R.id.title_container)
            mDescContainer = view!!.findViewById(R.id.desc_container)
            mStartDateContainer = view!!.findViewById(R.id.start_date_container)
            mStartTimeContainer = view!!.findViewById(R.id.start_time_container)
            mPlaceFromContainer = view!!.findViewById(R.id.place_from_container)
            mPlaceToContainer = view!!.findViewById(R.id.place_to_container)
            mDayOfTripContainer = view!!.findViewById(R.id.day_container)
            mJourneyModeContainer =  view!!.findViewById(R.id.journey_mode_container)
            val journeyModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, journeyModeEntries)
            mJourneyMode.setAdapter(journeyModeAdapter)
        }
    }
}