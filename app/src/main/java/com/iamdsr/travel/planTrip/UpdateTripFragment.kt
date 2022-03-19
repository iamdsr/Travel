package com.iamdsr.travel.planTrip

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.iamdsr.travel.R
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*

class UpdateTripFragment : Fragment() {

    // Widgets
    private var myView: View?= null
    private lateinit var mJourneyDate: EditText
    private lateinit var mReturnDate: EditText
    private lateinit var mAddTitle: EditText
    private lateinit var mAddDesc: EditText
    private lateinit var mWhereFrom: EditText
    private lateinit var mWhereTo: EditText
    private lateinit var mNumberOfPerson: EditText
    private lateinit var mJourneyMode: AutoCompleteTextView
    private lateinit var mUpdateTripBtn: Button
    private lateinit var clickedEditText: EditText
    private lateinit var progressBar: ProgressBar

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()
    private var titleBundle: String = ""
    private var descBundle: String = ""
    private var jDateBundle: String = ""
    private var rDateBundle: String = ""
    private var fromBundle: String = ""
    private var toBundle: String = ""
    private var journeyModeBundle: String = ""
    private var totalHeadsBundle: Long = -1
    private var tripIDBundle: String = ""
    private val journeyModeEntries = arrayOf("Flight", "Train", "Car")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            tripIDBundle = arguments!!.getString("TRIP_ID","")
            titleBundle = arguments!!.getString("TRIP_TITLE","")
            descBundle = arguments!!.getString("TRIP_DESCRIPTION","")
            jDateBundle = arguments!!.getString("JOURNEY_DATE","")
            rDateBundle = arguments!!.getString("RETURN_DATE","")
            fromBundle = arguments!!.getString("PLACE_FROM","")
            toBundle = arguments!!.getString("PLACE_TO","")
            journeyModeBundle = arguments!!.getString("JOURNEY_MODE","")
            totalHeadsBundle = arguments!!.getLong("TOTAL_PAX",-1)
        }
        setUpWidgets()
        setUpDateDialogs()
        mJourneyMode!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> mJourneyMode!!.showDropDown()
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
        mUpdateTripBtn.setOnClickListener(View.OnClickListener {
            updateTrip()
            findNavController().navigate(R.id.action_updateTripFragment_to_planTripFragment)
            Toast.makeText(context, "Congrats! Trip Update Successfully.", Toast.LENGTH_SHORT).show()
        })
    }

    private fun updateTrip() {
        val titleField = mAddTitle!!.text.toString().trim { it <= ' ' }
        val descField = mAddDesc!!.text.toString().trim { it <= ' ' }
        val fromField = mWhereFrom!!.text.toString().trim { it <= ' ' }
        val toField = mWhereTo!!.text.toString().trim { it <= ' ' }
        val jDateField = mJourneyDate!!.text.toString().trim { it <= ' ' }
        val rDateField = mReturnDate!!.text.toString().trim { it <= ' ' }
        val totalPersonField = mNumberOfPerson!!.text.toString().trim { it <= ' ' }
        val journeyModeField = mJourneyMode!!.text.toString().trim { it <= ' ' }

        if (!TextUtils.isEmpty(titleField) && !TextUtils.isEmpty(descField) && !TextUtils.isEmpty(fromField)
            && !TextUtils.isEmpty(toField) && !TextUtils.isEmpty(jDateField) && !TextUtils.isEmpty(rDateField) && !TextUtils.isEmpty(
                totalPersonField)  && !TextUtils.isEmpty(journeyModeField)) {
            if (titleBundle != titleField){
                updateAndPushTrip(titleField,null,null,null,null,null, null,-1)
            }
            if (descBundle != descField) {
                updateAndPushTrip(null, descField, null, null, null, null, null, -1)
            }
            if (fromBundle != fromField) {
                updateAndPushTrip(null, null, null, null, fromField, null, null, -1)
            }
            if (toBundle != toField) {
                updateAndPushTrip(null, null, null, null, null, toField, null, -1)
            }
            if (toBundle != toField) {
                updateAndPushTrip(null, null, null, null, null, toField, null, -1)
            }
            if (jDateBundle != jDateField || rDateBundle != rDateField) {
                updateAndPushTrip(null, null, jDateField, rDateField, null, null, null, -1)
            }
            if (totalHeadsBundle != totalPersonField.toLong()) {
                updateAndPushTrip(null, null, null, null, null, null, null, totalPersonField.toLong())
            }
            if (journeyModeBundle != journeyModeField) {
                updateAndPushTrip(null, null, null, null, null, null, journeyModeField, -1)
            }
        }
    }

    private fun updateAndPushTrip(
        title: String?,
        desc: String?,
        jDate: String?,
        rDate: String?,
        from: String?,
        to: String?,
        journeyMode: String?,
        pax: Long
    ) {
        val planTripFragmentViewModel = ViewModelProvider(this)[PlanTripFragmentViewModel::class.java]
        if (title != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["trip_title"] = title
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripIDBundle)
        }
        if (desc != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["trip_desc"] = desc
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripIDBundle)
        }
        if (jDate != null && rDate != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["journey_date"] = jDate
            tripMap["return_date"] = rDate
            tripMap["duration_in_days"] = getDateDiff(jDate, rDate)
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripIDBundle)
        }
        if (from != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["place_from"] = from
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripIDBundle)
        }
        if (to != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["place_to"] = to
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripIDBundle)
        }
        if (pax != -1L) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["total_heads"] = pax
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripIDBundle)
        }
        if (journeyMode != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["journey_mode"] = journeyMode
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripIDBundle)
        }
    }


    private fun setUpDateDialogs() {
        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar!![Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            clickedEditText?.let { updateLabel(it) }
        }
        mJourneyDate!!.setOnClickListener { view ->
            clickedEditText = view as EditText
            myCalendar?.let {
                DatePickerDialog(
                    context!!, date, it
                        .get(Calendar.YEAR), myCalendar!![Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }
        mReturnDate!!.setOnClickListener { view ->
            clickedEditText = view as EditText
            myCalendar?.let {
                DatePickerDialog(
                    context!!, date, it
                        .get(Calendar.YEAR), myCalendar!![Calendar.MONTH],
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

    private fun getDateDiff(CurrentDate: String, FinalDate: String): Long {
        return try {
            val date1: Date
            val date2: Date
            val myFormat = "dd/MM/yyyy"
            val dateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
            date1 = dateFormat.parse(FinalDate)
            date2 = dateFormat.parse(CurrentDate)
            val difference = Math.abs(date1.time - date2.time)
            difference / (24 * 60 * 60 * 1000)
        } catch (e: Exception) {
            0
        }
    }

    private fun setUpWidgets() {
        mAddTitle = view!!.findViewById(R.id.trip_title)
        mAddDesc = view!!.findViewById(R.id.trip_desc)
        mWhereFrom = view!!.findViewById(R.id.place_from)
        mWhereTo = view!!.findViewById(R.id.place_to)
        mNumberOfPerson = view!!.findViewById(R.id.total_person)
        mJourneyDate = view!!.findViewById(R.id.journey_date)
        mReturnDate = view!!.findViewById(R.id.return_date)
        mUpdateTripBtn = view!!.findViewById(R.id.update_trip_btn)
        mJourneyMode = view!!.findViewById(R.id.journey_mode)

        val journeyModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, journeyModeEntries)
        mJourneyMode.setAdapter(journeyModeAdapter)
        mJourneyMode.keyListener = null

        mJourneyMode.setText(journeyModeBundle)
        mAddTitle.setText(titleBundle)
        mAddDesc.setText(descBundle)
        mWhereFrom.setText(fromBundle)
        mWhereTo.setText(toBundle)
        mNumberOfPerson.setText(totalHeadsBundle.toString())
        mJourneyDate.setText(jDateBundle)
        mReturnDate.setText(rDateBundle)
    }
}