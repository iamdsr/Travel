package com.iamdsr.travel.planTrip

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
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
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.repositories.FirestoreRepository
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*


class AddTripFragment : Fragment() {

    // Widgets
    private lateinit var mJourneyDate: TextInputEditText
    private lateinit var mReturnDate:TextInputEditText
    private lateinit var mAddTitle:TextInputEditText
    private lateinit var mAddDesc:TextInputEditText
    private lateinit var mWhereFrom:TextInputEditText
    private lateinit var mWhereTo:TextInputEditText
    private lateinit var mNumberOfPerson:TextInputEditText
    private lateinit var mJourneyMode: AutoCompleteTextView

    private lateinit var mJourneyDateContainer: TextInputLayout
    private lateinit var mReturnDateContainer:TextInputLayout
    private lateinit var mAddTitleContainer:TextInputLayout
    private lateinit var mAddDescContainer:TextInputLayout
    private lateinit var mWhereFromContainer:TextInputLayout
    private lateinit var mWhereToContainer:TextInputLayout
    private lateinit var mNumberOfPersonContainer:TextInputLayout
    private lateinit var mJourneyModeContainer: TextInputLayout

    private lateinit var snack_layout: CoordinatorLayout

    private var mAddTripBtn: Button? = null
    private var progressBar: ProgressBar? = null

    // Utils
    private val journeyModeEntries = arrayOf("Flight", "Train", "Car")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWidgets()
        mJourneyDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })
        mReturnDate.setOnClickListener(View.OnClickListener {
            setUpDateDialogs(it as TextInputEditText)
        })
        mAddTripBtn!!.setOnClickListener {
            addNewTrip()
        }
    }


    private fun addNewTrip() {
        if (FirebaseAuth.getInstance().currentUser!=null) {
            val title = mAddTitle.text.toString().trim()
            val desc = mAddDesc.text.toString().trim()
            val from = mWhereFrom.text.toString().trim()
            val to = mWhereTo.text.toString().trim()
            val jDate = mJourneyDate.text.toString().trim()
            val rDate = mReturnDate.text.toString().trim()
            val totalPerson = mNumberOfPerson.text.toString().trim()
            val journeyMode = mJourneyMode.text.toString().trim()



            val planTripFragmentViewModel = ViewModelProvider(this)[PlanTripFragmentViewModel::class.java]
            if ((!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(from)
                        && !TextUtils.isEmpty(to) && !TextUtils.isEmpty(jDate) && !TextUtils.isEmpty(rDate) &&
                        !TextUtils.isEmpty(totalPerson) && !TextUtils.isEmpty(journeyMode))) {

                val error = checkDataOfInputFields(title, desc, from, to, jDate, rDate, totalPerson, journeyMode)
                Log.d("TAG", "addNewTrip: Error $error")

                if (!error){
                    progressBar?.visibility = View.VISIBLE
                    val firebaseRepository = FirestoreRepository()
                    val tripModel = TripModel(
                        firebaseRepository.getNewTripID(),
                        title,
                        desc,
                        jDate,
                        rDate,
                        from,
                        to,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        getTimestamp(),
                        journeyMode,
                        getDateDiff(jDate, rDate),
                        totalPerson.toLong()
                    )
                    planTripFragmentViewModel._addNewTripToFirebaseFirestore(tripModel)
                    findNavController().navigate(R.id.action_addTripFragment_to_planTripFragment)
                    progressBar?.visibility = View.INVISIBLE
                }
            }
            else {
                Snackbar.make(snack_layout, context!!.resources.getString(R.string.empty_fields_msg), Snackbar.LENGTH_LONG).show()
            }
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
    private fun getTimestamp(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getDefault()
        return format.format(Date())
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
        if (view != null){
            mAddTitle = view!!.findViewById(R.id.trip_title)
            mAddDesc = view!!.findViewById(R.id.trip_desc)
            mWhereFrom = view!!.findViewById(R.id.place_from)
            mWhereTo = view!!.findViewById(R.id.place_to)
            mNumberOfPerson = view!!.findViewById(R.id.total_person)
            mJourneyDate = view!!.findViewById(R.id.journey_date)
            mReturnDate = view!!.findViewById(R.id.return_date)
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
            mAddTripBtn = view!!.findViewById(R.id.add_trip_btn)
            progressBar = view!!.findViewById(R.id.progress_horizontal)
            val journeyModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, journeyModeEntries)
            mJourneyMode.setAdapter(journeyModeAdapter)
        }
    }
}