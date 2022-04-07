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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
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
        setUpDateDialogs()
        setUpWidgetValues()
        mChekInTime.setOnClickListener(View.OnClickListener {
            setUpTimeDialogs()
        })
        mUpdateItineraryButton.setOnClickListener(View.OnClickListener {
            updateItinerary()
        })
    }

    private fun parseStringDateTime(stringDateTime: String): Date {
        val pattern = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy h:mm a")
        val localDateTime = LocalDateTime.parse(stringDateTime, pattern)
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
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

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(checkInDate) && !TextUtils.isEmpty(checkInTime) && !TextUtils.isEmpty(hotelName) &&
            !TextUtils.isEmpty(hotelAddress) && !TextUtils.isEmpty(dayNumber)){
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

    private fun setUpWidgetValues() {
        mTitle.setText(itineraryModel.title)
        mDesc.setText(itineraryModel.description)
        mChekInDate.setText(itineraryModel.date)
        mChekInTime.setText(itineraryModel.time)
        mHotelName.setText(itineraryModel.hotel_name)
        mHotelLocation.setText(itineraryModel.hotel_address)
        mDayOfTrip.setText(itineraryModel.day.toString())
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
    private fun setUpDateDialogs() {
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar!![Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            Log.d("TAG", "setUpDateDialogs: Calendar called")
            updateLabel(mChekInDate)
        }
        mChekInDate.setOnClickListener {
            myCalendar?.let {
                DatePickerDialog(
                    context!!, date, it
                        .get(Calendar.YEAR), myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }
    }
    private fun updateLabel(view: EditText) {
        val myFormat = "EEE, MMM d, yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        view.setText(dateFormat.format(myCalendar!!.time))
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
        }
    }
}