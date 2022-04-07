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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.repositories.FirestoreRepository
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

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()
    private var tripID: String=""
    private var tripTitle: String=""
    private val journeyModeEntries = arrayOf("Flight", "Train", "Car")
    private var firebaseRepository = FirestoreRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWidgets()
        setUpDateDialogs()
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

            val timeObj = parseStringDateTime("$startDate $startDate")

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
    private fun setUpDateDialogs() {
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar!![Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            updateLabel(mStartDate)
        }
        mStartDate.setOnClickListener {
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
            mStartDate = view!!.findViewById(R.id.start_date)
            mStartTime = view!!.findViewById(R.id.start_time)
            mPlaceFrom = view!!.findViewById(R.id.place_from)
            mPlaceTo = view!!.findViewById(R.id.place_to)
            mJourneyMode = view!!.findViewById(R.id.journey_mode)
            mAddItineraryButton = view!!.findViewById(R.id.add_new_itinerary_btn)
            mDayOfTrip = view!!.findViewById(R.id.day)
            val journeyModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, journeyModeEntries)
            mJourneyMode.setAdapter(journeyModeAdapter)
            mJourneyMode.keyListener = null
        }
    }
}