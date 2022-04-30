package com.iamdsr.travel.planTrip

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.iamdsr.travel.R
import com.iamdsr.travel.databinding.FragmentUpdateTripBinding
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class UpdateTripFragment : Fragment() {

    // Utils
    private lateinit var tripModelBundle: TripModel
    private val journeyModeEntries = arrayOf("Flight", "Train", "Car")
    private lateinit var binding: FragmentUpdateTripBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_trip, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            tripModelBundle = requireArguments().getSerializable("TRIP_MODEL") as TripModel
        }
        setValuesToFields()
        binding.journeyDate.setOnClickListener{
            setUpDateDialogs(it as TextInputEditText)
        }
        binding.returnDate.setOnClickListener{
            setUpDateDialogs(it as TextInputEditText)
        }

        binding.updateTripBtn.setOnClickListener{
            updateTrip()
        }
    }

    private fun updateTrip() {
        val titleField = binding.tripTitle.text.toString().trim()
        val descField = binding.tripDesc.text.toString().trim()
        val fromField = binding.placeFrom.text.toString().trim()
        val toField = binding.placeTo.text.toString().trim()
        val jDateField = binding.journeyDate.text.toString().trim()
        val rDateField = binding.returnDate.text.toString().trim()
        val totalPersonField = binding.totalPerson.text.toString().trim()
        val journeyModeField = binding.journeyMode.text.toString().trim()

        if (!TextUtils.isEmpty(titleField) && !TextUtils.isEmpty(descField) && !TextUtils.isEmpty(fromField)
            && !TextUtils.isEmpty(toField) && !TextUtils.isEmpty(jDateField) && !TextUtils.isEmpty(rDateField)
            && !TextUtils.isEmpty(totalPersonField)  && !TextUtils.isEmpty(journeyModeField)) {

                val error = checkDataOfInputFields(titleField, descField, fromField, toField, jDateField, rDateField, totalPersonField, journeyModeField)
                Log.d("TAG", "addNewTrip: Error $error")

                if (!error){
                    if (tripModelBundle.trip_title != titleField){
                        updateAndPushTrip(titleField,null,null,null,null,null, null,-1)
                    }
                    if (tripModelBundle.trip_desc != descField) {
                        updateAndPushTrip(null, descField, null, null, null, null, null, -1)
                    }
                    if (tripModelBundle.place_from != fromField) {
                        updateAndPushTrip(null, null, null, null, fromField, null, null, -1)
                    }
                    if (tripModelBundle.place_to != toField) {
                        updateAndPushTrip(null, null, null, null, null, toField, null, -1)
                    }
                    if (tripModelBundle.journey_date != jDateField || tripModelBundle.return_date != rDateField) {
                        updateAndPushTrip(null, null, jDateField, rDateField, null, null, null, -1)
                    }
                    if (tripModelBundle.total_heads != totalPersonField.toLong()) {
                        updateAndPushTrip(null, null, null, null, null, null, null, totalPersonField.toLong())
                    }
                    if (tripModelBundle.journey_mode != journeyModeField) {
                        updateAndPushTrip(null, null, null, null, null, null, journeyModeField, -1)
                    }
                    Toast.makeText(context, "Congrats! Trip Update Successfully.", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
        }
        else {
            Snackbar.make(binding.snackbarLayout, requireContext().resources.getString(R.string.empty_fields_msg), Snackbar.LENGTH_LONG).show()
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
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripModelBundle.trip_id)
        }
        if (desc != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["trip_desc"] = desc
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripModelBundle.trip_id)
        }
        if (jDate != null && rDate != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["journey_date"] = jDate
            tripMap["return_date"] = rDate
            tripMap["duration_in_days"] = getDateDiff(jDate, rDate)
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripModelBundle.trip_id)
        }
        if (from != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["place_from"] = from
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripModelBundle.trip_id)
        }
        if (to != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["place_to"] = to
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripModelBundle.trip_id)
        }
        if (pax != -1L) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["total_heads"] = pax
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripModelBundle.trip_id)
        }
        if (journeyMode != null) {
            val tripMap: MutableMap<String, Any> = HashMap()
            tripMap["journey_mode"] = journeyMode
            planTripFragmentViewModel._updateTripToFirebaseFirestore(tripMap, tripModelBundle.trip_id)
        }
    }
    private fun checkDataOfInputFields(title: String, desc: String, from: String, to: String, jDate: String, rDate: String, pax: String, jMode: String): Boolean{
        var errorFound = false
        if (title.length > 75){
            errorFound = true
            binding.tripTitleContainer.error = requireContext().getString(R.string.length_exceeds_error, 75)
        }
        else {
            binding.tripTitleContainer.error = null
        }
        if (desc.length > 300){
            errorFound = true
            binding.tripDescContainer.error = requireContext().getString(R.string.length_exceeds_error, 300)
        }
        else {
            binding.tripDescContainer.error = null
        }
        if (checkDates(jDate, rDate) != ""){
            errorFound = true
            binding.returnDateContainer.error = checkDates(jDate, rDate)
            binding.returnDateContainer.error = checkDates(jDate, rDate)
        }
        else {
            binding.returnDateContainer.error = null
            binding.returnDateContainer.error = null
        }
        if (from.length > 20){
            errorFound = true
            binding.placeFromContainer.error =requireContext().getString(R.string.length_exceeds_error, 20)
        }
        else {
            binding.placeFromContainer.error = null
        }
        if (to.length > 20){
            errorFound = true
            binding.placeToContainer.error = requireContext().getString(R.string.length_exceeds_error, 20)
        }
        else {
            binding.placeToContainer.error = null
        }
        if (pax.toInt() < 0){
            errorFound = true
            binding.totalPersonContainer.error =requireContext().getString(R.string.invalid_value_error, pax)
        }
        else {
            binding.totalPersonContainer.error = null
        }
        if (!journeyModeEntries.contains(jMode)){
            Log.d("TAG", "addNewTrip: Error $jMode")
            errorFound = true
            binding.journeyModeContainer.error = requireContext().getString(R.string.invalid_value_error, jMode)
        }
        else {
            binding.journeyModeContainer.error = null
        }
        return errorFound
    }
    private fun checkDates(startDate: String, endDate: String): String? {
        val sdf = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
        try {
            when {
                sdf.parse(endDate)!!.before(sdf.parse(startDate)) -> {
                    return requireContext().resources.getString(R.string.end_date_l_start_date)
                }
                sdf.parse(endDate)!!.after(sdf.parse(startDate)) -> {
                    return ""
                }
                sdf.parse(endDate)!! == (sdf.parse(startDate)) -> {
                    return requireContext().resources.getString(R.string.end_date_e_start_date)
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
        binding.tripTitle.setText(tripModelBundle.trip_title)
        binding.tripDesc.setText(tripModelBundle.trip_desc)
        binding.placeFrom.setText(tripModelBundle.place_from)
        binding.placeTo.setText(tripModelBundle.place_to)
        binding.totalPerson.setText(tripModelBundle.total_heads.toString())
        binding.journeyDate.setText(tripModelBundle.journey_date)
        binding.returnDate.setText(tripModelBundle.return_date)
        binding.journeyMode.setText(tripModelBundle.journey_mode)
        val journeyModeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, journeyModeEntries)
        binding.journeyMode.setAdapter(journeyModeAdapter)
    }

}