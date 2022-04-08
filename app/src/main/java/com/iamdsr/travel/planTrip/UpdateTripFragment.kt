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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.iamdsr.travel.R
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

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

    private lateinit var snack_layout: CoordinatorLayout

    private lateinit var mJourneyDateContainer: TextInputLayout
    private lateinit var mReturnDateContainer: TextInputLayout
    private lateinit var mAddTitleContainer: TextInputLayout
    private lateinit var mAddDescContainer: TextInputLayout
    private lateinit var mWhereFromContainer: TextInputLayout
    private lateinit var mWhereToContainer: TextInputLayout
    private lateinit var mNumberOfPersonContainer: TextInputLayout
    private lateinit var mJourneyModeContainer: TextInputLayout

    // Utils
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
        setValuesToFields()
        mJourneyDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })
        mReturnDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })

        mUpdateTripBtn.setOnClickListener(View.OnClickListener {
            updateTrip()
        })
    }

    private fun updateTrip() {
        val titleField = mAddTitle.text.toString().trim()
        val descField = mAddDesc.text.toString().trim()
        val fromField = mWhereFrom.text.toString().trim()
        val toField = mWhereTo.text.toString().trim()
        val jDateField = mJourneyDate.text.toString().trim()
        val rDateField = mReturnDate.text.toString().trim()
        val totalPersonField = mNumberOfPerson.text.toString().trim()
        val journeyModeField = mJourneyMode.text.toString().trim()

        if (!TextUtils.isEmpty(titleField) && !TextUtils.isEmpty(descField) && !TextUtils.isEmpty(fromField)
            && !TextUtils.isEmpty(toField) && !TextUtils.isEmpty(jDateField) && !TextUtils.isEmpty(rDateField)
            && !TextUtils.isEmpty(totalPersonField)  && !TextUtils.isEmpty(journeyModeField)) {

                val error = checkDataOfInputFields(titleField, descField, fromField, toField, jDateField, rDateField, totalPersonField, journeyModeField)
                Log.d("TAG", "addNewTrip: Error $error")

                if (!error){
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
                    Toast.makeText(context, "Congrats! Trip Update Successfully.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
        }
        else {
            Snackbar.make(snack_layout, context!!.resources.getString(R.string.empty_fields_msg), Snackbar.LENGTH_LONG).show()
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
    private fun checkDataOfInputFields(title: String, desc: String, from: String, to: String, jDate: String, rDate: String, pax: String, jMode: String): Boolean{
        var errorFound = false
        if (title.length > 75){
            errorFound = true
            mAddTitleContainer.error = context!!.getString(R.string.length_exceeds_error, 75)
        }
        else {
            mAddTitleContainer.error = null
        }
        if (desc.length > 300){
            errorFound = true
            mAddDescContainer.error = context!!.getString(R.string.length_exceeds_error, 300)
        }
        else {
            mAddDescContainer.error = null
        }
        if (checkDates(jDate, rDate) != ""){
            errorFound = true
            mJourneyDateContainer.error = checkDates(jDate, rDate)
            mReturnDateContainer.error = checkDates(jDate, rDate)
        }
        else {
            mJourneyDateContainer.error = null
            mReturnDateContainer.error = null
        }
        if (from.length > 20){
            errorFound = true
            mWhereFromContainer.error = context!!.getString(R.string.length_exceeds_error, 20)
        }
        else {
            mWhereFromContainer.error = null
        }
        if (to.length > 20){
            errorFound = true
            mWhereToContainer.error = context!!.getString(R.string.length_exceeds_error, 20)
        }
        else {
            mWhereToContainer.error = null
        }
        if (pax.toInt() < 0){
            errorFound = true
            mNumberOfPersonContainer.error = context!!.getString(R.string.invalid_value_error, pax)
        }
        else {
            mNumberOfPersonContainer.error = null
        }
        if (!journeyModeEntries.contains(jMode)){
            Log.d("TAG", "addNewTrip: Error $jMode")
            errorFound = true
            mJourneyModeContainer.error = context!!.getString(R.string.invalid_value_error, jMode)
        }
        else {
            mJourneyModeContainer.error = null
        }
        return errorFound
    }
    private fun checkDates(startDate: String, endDate: String): String? {
        val sdf = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
        try {
            when {
                sdf.parse(endDate)!!.before(sdf.parse(startDate)) -> {
                    return context!!.resources.getString(R.string.end_date_l_start_date)
                }
                sdf.parse(endDate)!!.after(sdf.parse(startDate)) -> {
                    return ""
                }
                sdf.parse(endDate)!! == (sdf.parse(startDate)) -> {
                    return context!!.resources.getString(R.string.end_date_e_start_date)
                }
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }
    private fun setUpDateDialogs(view: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker().setTheme(R.style.ThemeOverlay_App_DatePicker).build()
        datePicker.show(parentFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
    private fun getDateDiff(CurrentDate: String, FinalDate: String): Long {
        return try {
            val date1: Date?
            val date2: Date?
            val myFormat = "dd/MM/yyyy"
            val dateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
            date1 = dateFormat.parse(FinalDate)
            date2 = dateFormat.parse(CurrentDate)
            val difference = abs(date1.time - date2.time)
            difference / (24 * 60 * 60 * 1000)
        } catch (e: Exception) {
            0
        }
    }
    private fun setValuesToFields(){
        mJourneyMode.setText(journeyModeBundle)
        mAddTitle.setText(titleBundle)
        mAddDesc.setText(descBundle)
        mWhereFrom.setText(fromBundle)
        mWhereTo.setText(toBundle)
        mNumberOfPerson.setText(totalHeadsBundle.toString())
        mJourneyDate.setText(jDateBundle)
        mReturnDate.setText(rDateBundle)
    }
    private fun setUpWidgets() {
        if (view!=null){
            mAddTitle = view!!.findViewById(R.id.trip_title)
            mAddDesc = view!!.findViewById(R.id.trip_desc)
            mWhereFrom = view!!.findViewById(R.id.place_from)
            mWhereTo = view!!.findViewById(R.id.place_to)
            mNumberOfPerson = view!!.findViewById(R.id.total_person)
            mJourneyDate = view!!.findViewById(R.id.journey_date)
            mReturnDate = view!!.findViewById(R.id.return_date)
            mUpdateTripBtn = view!!.findViewById(R.id.update_trip_btn)
            mJourneyMode = view!!.findViewById(R.id.journey_mode)

            mAddTitleContainer = view!!.findViewById(R.id.trip_title_container)
            mAddDescContainer = view!!.findViewById(R.id.trip_desc_container)
            mWhereFromContainer = view!!.findViewById(R.id.place_from_container)
            mWhereToContainer = view!!.findViewById(R.id.place_to_container)
            mNumberOfPersonContainer = view!!.findViewById(R.id.total_person_container)
            mJourneyDateContainer = view!!.findViewById(R.id.journey_date_container)
            mReturnDateContainer = view!!.findViewById(R.id.return_date_container)
            mJourneyModeContainer = view!!.findViewById(R.id.journey_mode_container)

            snack_layout = view!!.findViewById(R.id.snackbar_layout)

            val journeyModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, journeyModeEntries)
            mJourneyMode.setAdapter(journeyModeAdapter)
        }
    }
}