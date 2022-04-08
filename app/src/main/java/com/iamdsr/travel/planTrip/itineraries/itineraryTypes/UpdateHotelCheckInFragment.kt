package com.iamdsr.travel.planTrip.itineraries.itineraryTypes

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
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


class UpdateHotelCheckInFragment : Fragment() {

    // Widgets
    private lateinit var mTitle: TextInputEditText
    private lateinit var mDesc: TextInputEditText
    private lateinit var mChekInDate: TextInputEditText
    private lateinit var mChekInTime: TextInputEditText
    private lateinit var mHotelName: TextInputEditText
    private lateinit var mHotelLocation: TextInputEditText
    private lateinit var mDayOfTrip: TextInputEditText
    private lateinit var mUpdateItineraryButton: Button

    private lateinit var mTitleContainer: TextInputLayout
    private lateinit var mDescContainer: TextInputLayout
    private lateinit var mChekInDateContainer: TextInputLayout
    private lateinit var mChekInTimeContainer: TextInputLayout
    private lateinit var mHotelNameContainer: TextInputLayout
    private lateinit var mHotelLocationContainer: TextInputLayout
    private lateinit var mDayOfTripContainer: TextInputLayout

    private lateinit var snack_layout: CoordinatorLayout

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()
    private var itineraryModel = ItineraryModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments != null){
            itineraryModel = arguments!!.getSerializable("OBJ") as ItineraryModel
        }
        return inflater.inflate(R.layout.fragment_update_hotel_check_in, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setUpWidgetValues()

        mChekInDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })
        mChekInTime.setOnClickListener(View.OnClickListener {
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
            mChekInTime.setText(formattedTime.trim())
        }
    }
    private fun setUpDateDialogs(view: TextInputEditText) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker().setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build()
        datePicker.show(parentFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            val date = dateFormatter.format(Date(it))
            view.setText(date)
        }
        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(context, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG)
                .show()
        }
        datePicker.addOnCancelListener {
            Toast.makeText(context, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateItinerary() {
        val title = mTitle.text.toString().trim()
        val desc = mDesc.text.toString().trim()
        val checkInDate = mChekInDate.text.toString().trim()
        val checkInTime = mChekInTime.text.toString().trim()
        val hotelName = mHotelName.text.toString().trim()
        val hotelAddress = mHotelLocation.text.toString().trim()
        val dayNumber = mDayOfTrip.text.toString().trim()

        val timeObj = parseStringDateTime("$checkInDate $checkInTime")

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(checkInDate)
            && !TextUtils.isEmpty(checkInTime) && !TextUtils.isEmpty(hotelName) &&
            !TextUtils.isEmpty(hotelAddress) && !TextUtils.isEmpty(dayNumber)){

            val error = checkDataOfInputFields(title, desc, checkInDate, checkInTime, hotelName, hotelAddress, dayNumber)
            Log.d("TAG", "addNewTrip: Error $error")

            if (!error){
                if (itineraryModel.title != title){
                    pushUpdateToFirestore(title, null, null, null, null, null,-1, timeObj)
                }
                if (itineraryModel.description != desc){
                    pushUpdateToFirestore(null, desc, null, null, null, null,-1, timeObj)
                }
                if (itineraryModel.date != checkInDate){
                    pushUpdateToFirestore(null, null, checkInDate, null, null, null,-1, timeObj)
                }
                if (itineraryModel.time != checkInTime){
                    pushUpdateToFirestore(null, null, null, checkInTime, null, null,-1, timeObj)
                }
                if (itineraryModel.hotel_name != hotelName){
                    pushUpdateToFirestore(null, null, null, null, hotelName, null,-1, timeObj)
                }
                if (itineraryModel.hotel_address != hotelAddress){
                    pushUpdateToFirestore(null, null, null, null, null, hotelAddress,-1, timeObj)
                }
                if (itineraryModel.day.toString() != dayNumber){
                    pushUpdateToFirestore(null, null, null, null, null, null,dayNumber.toLong(), timeObj)
                }
                findNavController().navigateUp()
            }
        }
    }
    private fun pushUpdateToFirestore(title: String?, desc: String?, date: String?, time: String?, hotelName: String?, hotelAddress: String?, day: Long, timeObj: Date){
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
        if (hotelName != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["hotel_name"] = hotelName
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (hotelAddress != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["hotel_address"] = hotelAddress
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (day != -1L) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["day"] = day
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
    }
    private fun checkDataOfInputFields(title: String, desc: String, checkInDate: String, checkInTime: String, hotelName: String, hotelAddress: String, day: String): Boolean{
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
        if (hotelName.length > 30){
            errorFound = true
            mHotelNameContainer.error = context!!.getString(R.string.length_exceeds_error, 30)
        }
        else {
            mHotelNameContainer.error = null
        }
        if (hotelAddress.length > 50){
            errorFound = true
            mHotelLocationContainer.error = context!!.getString(R.string.length_exceeds_error, 50)
        }
        else {
            mHotelLocationContainer.error = null
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
    private fun parseStringDateTime(stringDateTime: String): Date {
        val pattern = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a")
        val localDateTime = LocalDateTime.parse(stringDateTime, pattern)
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

    private fun setupWidgets() {
        if (view != null){
            mTitle = view!!.findViewById(R.id.title)
            mDesc = view!!.findViewById(R.id.desc)
            mChekInDate = view!!.findViewById(R.id.check_in_date)
            mChekInTime = view!!.findViewById(R.id.check_in_time)
            mHotelName = view!!.findViewById(R.id.hotel_name)
            mHotelLocation = view!!.findViewById(R.id.hotel_address)
            mUpdateItineraryButton = view!!.findViewById(R.id.update_itinerary_btn)
            mDayOfTrip = view!!.findViewById(R.id.day)

            snack_layout = view!!.findViewById(R.id.snackbar_layout)
            mTitleContainer = view!!.findViewById(R.id.title_container)
            mDescContainer = view!!.findViewById(R.id.desc_container)
            mChekInDateContainer = view!!.findViewById(R.id.date_container)
            mChekInTimeContainer = view!!.findViewById(R.id.time_container)
            mHotelNameContainer = view!!.findViewById(R.id.hotel_name_container)
            mHotelLocationContainer = view!!.findViewById(R.id.hotel_location_container)
            mDayOfTripContainer = view!!.findViewById(R.id.day_container)
        }
    }
    private fun setUpWidgetValues() {
        mTitle.setText(itineraryModel.title)
        mDesc.setText(itineraryModel.description)
        mChekInDate.setText(itineraryModel.date)
        mChekInTime.setText(itineraryModel.time)
        mHotelName.setText(itineraryModel.hotel_name)
        mHotelLocation.setText(itineraryModel.hotel_address)
        mDayOfTrip.setText(itineraryModel.day.toString())
    }

}