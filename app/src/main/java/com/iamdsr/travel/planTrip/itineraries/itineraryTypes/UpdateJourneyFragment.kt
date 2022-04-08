package com.iamdsr.travel.planTrip.itineraries.itineraryTypes

import android.app.DatePickerDialog
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
import com.iamdsr.travel.R
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.viewModels.ItineraryTimelineViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class UpdateJourneyFragment : Fragment() {

    // Widgets
    private lateinit var mTitle: TextInputEditText
    private lateinit var mDesc: TextInputEditText
    private lateinit var mStartDate: TextInputEditText
    private lateinit var mStartTime: TextInputEditText
    private lateinit var mPlaceFrom: TextInputEditText
    private lateinit var mPlaceTo: TextInputEditText
    private lateinit var mDayOfTrip: TextInputEditText
    private lateinit var mJourneyMode: AutoCompleteTextView
    private lateinit var mUpdateItineraryButton: Button

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
    private var itineraryModel = ItineraryModel()
    private val journeyModeEntries = arrayOf("Flight", "Train", "Car")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (arguments != null){
            itineraryModel = arguments!!.getSerializable("OBJ") as ItineraryModel
        }
        return inflater.inflate(R.layout.fragment_update_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setUpWidgetValues()

        mStartDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })
        mStartTime.setOnClickListener(View.OnClickListener {
            setUpTimeDialogs()
        })
        mUpdateItineraryButton.setOnClickListener(View.OnClickListener {
            updateItinerary()
        })
    }

    private fun setUpTimeDialogs(){
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Select Hotel Check-In Time")
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
            mStartTime.setText(formattedTime.trim())
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

    private fun updateItinerary() {
        val title = mTitle.text.toString().trim()
        val desc = mDesc.text.toString().trim()
        val startDate = mStartDate.text.toString().trim()
        val startTime = mStartTime.text.toString().trim()
        val placeFrom = mPlaceFrom.text.toString().trim()
        val placeTo = mPlaceTo.text.toString().trim()
        val journeyMode = mJourneyMode.text.toString().trim()
        val dayNumber = mDayOfTrip.text.toString().trim()

        val timeObj = parseStringDateTime("$startDate $startTime")

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(placeFrom) &&
            !TextUtils.isEmpty(placeTo) && !TextUtils.isEmpty(dayNumber) && !TextUtils.isEmpty(journeyMode)){

            val error = checkDataOfInputFields(title, desc, startDate, startTime, placeFrom, placeTo, journeyMode, dayNumber)
            Log.d("TAG", "addNewTrip: Error:$error-$journeyMode")

            if (!error){
                if (itineraryModel.title != title){
                    pushUpdateToFirestore(title, null, null, null, null, null,-1, null, timeObj)
                }
                if (itineraryModel.description != desc){
                    pushUpdateToFirestore(null, desc, null, null, null, null,-1,null, timeObj)
                }
                if (itineraryModel.date != startDate){
                    pushUpdateToFirestore(null, null, startDate, null, null, null,-1,null, timeObj)
                }
                if (itineraryModel.time != startTime){
                    pushUpdateToFirestore(null, null, null, startTime, null, null,-1,null, timeObj)
                }
                if (itineraryModel.from != placeFrom){
                    pushUpdateToFirestore(null, null, null, null, placeFrom, null,-1,null, timeObj)
                }
                if (itineraryModel.to != placeTo){
                    pushUpdateToFirestore(null, null, null, null, null, placeTo,-1,null, timeObj)
                }
                if (itineraryModel.journey_mode != journeyMode){
                    pushUpdateToFirestore(null, null, null, null, null, null,dayNumber.toLong(),journeyMode, timeObj)
                }
                if (itineraryModel.day.toString() != dayNumber){
                    pushUpdateToFirestore(null, null, null, null, null, null,dayNumber.toLong(),null, timeObj)
                }
                findNavController().navigateUp()
            }
        }
        else {
            Snackbar.make(snack_layout, context!!.resources.getString(R.string.empty_fields_msg), Snackbar.LENGTH_LONG).show()
        }
    }
    private fun pushUpdateToFirestore(title: String?, desc: String?, date: String?, time: String?, placeFrom: String?, placeTo: String?, day: Long, journeyMode: String?, timeObj: Date){
        val itineraryTimelineViewModel = ViewModelProvider(this)[ItineraryTimelineViewModel::class.java]
        if (title != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["title"] = title
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (desc != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["description"] = desc
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (date != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["date"] = date
            itineraryMap["technical_time"] = timeObj
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (time != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["time"] = time
            itineraryMap["technical_time"] = timeObj
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (placeFrom != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["from"] = placeFrom
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (placeTo != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["to"] = placeTo
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (journeyMode != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["journey_mode"] = journeyMode
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (day != -1L) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["day"] = day
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
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

    private fun setupWidgets() {
        if (view != null){
            mTitle = view!!.findViewById(R.id.title)
            mDesc = view!!.findViewById(R.id.desc)
            mStartDate = view!!.findViewById(R.id.start_date)
            mStartTime = view!!.findViewById(R.id.start_time)
            mPlaceFrom = view!!.findViewById(R.id.place_from)
            mPlaceTo = view!!.findViewById(R.id.place_to)
            mJourneyMode = view!!.findViewById(R.id.journey_mode)
            val journeyModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, journeyModeEntries)
            mJourneyMode.setAdapter(journeyModeAdapter)

            snack_layout = view!!.findViewById(R.id.snackbar_layout)
            mTitleContainer = view!!.findViewById(R.id.title_container)
            mDescContainer = view!!.findViewById(R.id.desc_container)
            mStartDateContainer = view!!.findViewById(R.id.start_date_container)
            mStartTimeContainer = view!!.findViewById(R.id.start_time_container)
            mPlaceFromContainer = view!!.findViewById(R.id.place_from_container)
            mPlaceToContainer = view!!.findViewById(R.id.place_to_container)
            mDayOfTripContainer = view!!.findViewById(R.id.day_container)
            mJourneyModeContainer =  view!!.findViewById(R.id.journey_mode_container)
            mUpdateItineraryButton = view!!.findViewById(R.id.update_itinerary_btn)
            mDayOfTrip = view!!.findViewById(R.id.day)
        }
    }
    private fun setUpWidgetValues() {
        mTitle.setText(itineraryModel.title)
        mDesc.setText(itineraryModel.description)
        mStartDate.setText(itineraryModel.date)
        mStartTime.setText(itineraryModel.time)
        mPlaceFrom.setText(itineraryModel.from)
        mPlaceTo.setText(itineraryModel.to)
        mDayOfTrip.setText(itineraryModel.day.toString())
        mJourneyMode.setText(itineraryModel.journey_mode)
    }

}