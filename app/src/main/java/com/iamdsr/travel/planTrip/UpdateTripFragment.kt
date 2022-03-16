package com.iamdsr.travel.planTrip

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
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
    private var totalHeadsBundle: Long = -1
    private var tripIDBundle: String = ""

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
            totalHeadsBundle = arguments!!.getLong("TOTAL_PAX",-1)
        }
        setUpWidgets()
        setUpDateDialogs()
        mUpdateTripBtn?.setOnClickListener(View.OnClickListener {
            updateTripToDB()
        })
    }

    private fun updateTripToDB() {
        val titleField = mAddTitle!!.text.toString().trim { it <= ' ' }
        val descField = mAddDesc!!.text.toString().trim { it <= ' ' }
        val fromField = mWhereFrom!!.text.toString().trim { it <= ' ' }
        val toField = mWhereTo!!.text.toString().trim { it <= ' ' }
        val jDateField = mJourneyDate!!.text.toString().trim { it <= ' ' }
        val rDateField = mReturnDate!!.text.toString().trim { it <= ' ' }
        val totalPersonField = mNumberOfPerson!!.text.toString().trim { it <= ' ' }

        if (!TextUtils.isEmpty(titleField) && !TextUtils.isEmpty(descField) && !TextUtils.isEmpty(fromField)
            && !TextUtils.isEmpty(toField) && !TextUtils.isEmpty(jDateField) && !TextUtils.isEmpty(rDateField) && !TextUtils.isEmpty(
                totalPersonField
            )
        ) {
            if (titleBundle != titleField){
                updateTrip(titleField,null,null,null,null,null,-1)
            }
            if (descBundle != descField) {
                updateTrip(null, descField, null, null, null, null, -1)
            }
            if (fromBundle != fromField) {
                updateTrip(null, null, null, null, fromField, null, -1)
            }
            if (toBundle != toField) {
                updateTrip(null, null, null, null, null, toField, -1)
            }
            if (jDateBundle != jDateField || rDateBundle != rDateField) {
                updateTrip(null, null, jDateField, rDateField, null, null, -1)
            }
            if (totalHeadsBundle != totalPersonField.toLong()) {
                updateTrip(null, null, null, null, null, null, totalPersonField.toLong())
            }
        }
    }

    private fun updateTrip(
        title: String?,
        desc: String?,
        jDate: String?,
        rDate: String?,
        from: String?,
        to: String?,
        pax: Long
    ) {
        val planTripFragmentViewModel = ViewModelProvider(this)[PlanTripFragmentViewModel::class.java]
        if (title != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["trip_title"] = title
            planTripFragmentViewModel.updateTripToFirebase(tripMap, tripIDBundle)
        }
        if (desc != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["trip_desc"] = desc
            planTripFragmentViewModel.updateTripToFirebase(tripMap, tripIDBundle)
        }
        if (jDate != null && rDate != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["journey_date"] = jDate
            tripMap["return_date"] = rDate
            tripMap["duration_in_days"] = getDateDiff(jDate, rDate)
            planTripFragmentViewModel.updateTripToFirebase(tripMap, tripIDBundle)
        }
        if (from != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["place_from"] = from
            planTripFragmentViewModel.updateTripToFirebase(tripMap, tripIDBundle)
        }
        if (to != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["place_to"] = to
            planTripFragmentViewModel.updateTripToFirebase(tripMap, tripIDBundle)
        }
        if (pax != -1L) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["total_heads"] = pax
            planTripFragmentViewModel.updateTripToFirebase(tripMap, tripIDBundle)
        }
        findNavController().navigate(R.id.action_updateTripFragment_to_planTripFragment)
        Toast.makeText(context, "Congrats! Trip Update Successfully.", Toast.LENGTH_SHORT).show()
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

        mAddTitle.setText(titleBundle)
        mAddDesc.setText(descBundle)
        mWhereFrom.setText(fromBundle)
        mWhereTo.setText(toBundle)
        mNumberOfPerson.setText(totalHeadsBundle.toString())
        mJourneyDate.setText(jDateBundle)
        mReturnDate.setText(rDateBundle)
    }
}