package com.iamdsr.travel.planTrip.itineraries.itineraryTypes

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.iamdsr.travel.R
import com.iamdsr.travel.models.ItineraryModel
import java.text.SimpleDateFormat
import java.util.*

class AddHotelCheckInFragment : Fragment() {

    // Widgets
    lateinit var mTitle: TextInputEditText
    lateinit var mDesc: TextInputEditText
    lateinit var mChekInDate: TextInputEditText
    lateinit var mChekInTime: TextInputEditText
    lateinit var mHotelName: TextInputEditText
    lateinit var mHotelLocation: TextInputEditText
    private lateinit var mAddItineraryButton: Button

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_hotel_check_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setUpDateDialogs()
        mChekInTime.setOnClickListener(View.OnClickListener {
            setUpTimeDialogs()
        })
        mAddItineraryButton.setOnClickListener(View.OnClickListener {
            val title: String = mTitle.text.toString().trim()
            val desc: String = mTitle.text.toString().trim()
            val checkInDate: String = mTitle.text.toString().trim()
            val checkInTime: String = mTitle.text.toString().trim()
            val hotelName: String = mTitle.text.toString().trim()
            val hotelAddress: String = mTitle.text.toString().trim()
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(checkInDate) || TextUtils.isEmpty(checkInTime) ||
                TextUtils.isEmpty(hotelName) || TextUtils.isEmpty(hotelAddress)) {
                val itineraryModel = ItineraryModel(
                    "",
                    title,
                    desc,
                    checkInDate,
                    checkInTime,
                    0,
                    "HOTEL_CHECK_IN",
                    "",
                    "",
                    "",
                    hotelName,
                    hotelAddress,
                    getTimestamp(),
                    "",
                    "",
                    "",
                    0,
                )
            }
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
            mChekInTime.setText(formattedTime)
        }
    }
    private fun setUpDateDialogs() {
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar!![Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
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
        val myFormat = "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        view.setText(dateFormat.format(myCalendar!!.time))
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
            mChekInDate = view!!.findViewById(R.id.check_in_date)
            mChekInTime = view!!.findViewById(R.id.check_in_time)
            mHotelName = view!!.findViewById(R.id.hotel_name)
            mHotelLocation = view!!.findViewById(R.id.hotel_address)
            mAddItineraryButton = view!!.findViewById(R.id.add_new_itinerary_btn)
        }
    }
}