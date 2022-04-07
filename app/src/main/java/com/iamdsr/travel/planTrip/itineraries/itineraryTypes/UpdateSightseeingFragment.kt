package com.iamdsr.travel.planTrip.itineraries.itineraryTypes

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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


class UpdateSightseeingFragment : Fragment() {


    // Widgets
    private lateinit var mTitle: TextInputEditText
    private lateinit var mDesc: TextInputEditText
    private lateinit var mVisitDate: TextInputEditText
    private lateinit var mVisitTime: TextInputEditText
    private lateinit var mSiteName: TextInputEditText
    private lateinit var mSiteAddress: TextInputEditText
    private lateinit var mDayOfTrip: TextInputEditText
    private lateinit var mUpdateItineraryButton: Button

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()
    private var itineraryModel = ItineraryModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments != null){
            itineraryModel = arguments!!.getSerializable("OBJ") as ItineraryModel
        }
        return inflater.inflate(R.layout.fragment_update_sightseeing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setUpDateDialogs()
        setUpWidgetValues()
        mVisitTime.setOnClickListener(View.OnClickListener {
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
        val visitDate = mVisitDate.text.toString().trim()
        val visitTime = mVisitTime.text.toString().trim()
        val siteName = mSiteName.text.toString().trim()
        val siteAddress = mSiteAddress.text.toString().trim()
        val dayNumber = mDayOfTrip.text.toString().trim()

        val timeObj = parseStringDateTime("$visitDate $visitTime")

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(visitDate) && !TextUtils.isEmpty(visitTime) && !TextUtils.isEmpty(siteName) &&
            !TextUtils.isEmpty(siteAddress) && !TextUtils.isEmpty(dayNumber)){
            if (itineraryModel.title != title){
                pushUpdateToFirestore(title, null, null, null, null, null,-1, timeObj)
            }
            if (itineraryModel.description != desc){
                pushUpdateToFirestore(null, desc, null, null, null, null,-1, timeObj)
            }
            if (itineraryModel.date != visitDate){
                pushUpdateToFirestore(null, null, visitDate, null, null, null,-1, timeObj)
            }
            if (itineraryModel.time != visitTime){
                pushUpdateToFirestore(null, null, null, visitTime, null, null,-1, timeObj)
            }
            if (itineraryModel.sight_name != siteName){
                pushUpdateToFirestore(null, null, null, null, siteName, null,-1, timeObj)
            }
            if (itineraryModel.sight_address != siteAddress){
                pushUpdateToFirestore(null, null, null, null, null, siteAddress,-1, timeObj)
            }
            if (itineraryModel.day.toString() != dayNumber){
                pushUpdateToFirestore(null, null, null, null, null, null,dayNumber.toLong(), timeObj)
            }
        }
    }

    private fun pushUpdateToFirestore(title: String?, desc: String?, date: String?, time: String?,
                                      sightName: String?, sightAddress: String?, day: Long, timeObj: Date){
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
        if (sightName != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["sight_name"] = sightName
            itineraryTimelineViewModel.updateItinerary(itineraryMap, itineraryModel)
        }
        if (sightAddress != null) {
            val itineraryMap: MutableMap<String, Any> = HashMap()
            itineraryMap["sight_address"] = sightAddress
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
        mVisitDate.setText(itineraryModel.date)
        mVisitTime.setText(itineraryModel.time)
        mSiteName.setText(itineraryModel.sight_name)
        mSiteAddress.setText(itineraryModel.sight_address)
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
            mVisitTime.setText(formattedTime.trim())
        }
    }
    private fun setUpDateDialogs() {
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar!![Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            Log.d("TAG", "setUpDateDialogs: Calendar called")
            updateLabel(mVisitDate)
        }
        mVisitDate.setOnClickListener {
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
            mVisitDate = view!!.findViewById(R.id.date)
            mVisitTime = view!!.findViewById(R.id.time)
            mSiteName = view!!.findViewById(R.id.place_name)
            mSiteAddress = view!!.findViewById(R.id.place_address)
            mUpdateItineraryButton = view!!.findViewById(R.id.update_itinerary_btn)
            mDayOfTrip = view!!.findViewById(R.id.day)
        }
    }

}