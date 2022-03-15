package com.iamdsr.travel.planTrip

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.iamdsr.travel.R
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.repositories.FirestoreRepository
import com.iamdsr.travel.viewModels.PlanTripFragmentViewModel
import java.text.SimpleDateFormat
import java.util.*


class AddTripFragment : Fragment() {

    // Widgets
    private var myView: View?= null
    private var mJourneyDate: EditText? = null
    private var mReturnDate:EditText? = null
    private var mAddTitle:EditText? = null
    private var mAddDesc:EditText? = null
    private var mWhereFrom:EditText? = null
    private var mWhereTo:EditText? = null
    private var mNumberOfPerson:EditText? = null
    private var mAddTripBtn: Button? = null
    private var clickedEditText: EditText? = null
    private var progressBar: ProgressBar? = null

    // Utils
    private val myCalendar: Calendar? = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myView = inflater.inflate(R.layout.fragment_add_trip, container, false)
        setUpWidgets()
        setUpDateDialogs()
        mAddTripBtn!!.setOnClickListener {
            addTripToDB()
        }

        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    private fun addTripToDB() {
        if (FirebaseAuth.getInstance().currentUser!=null) {
            val title = mAddTitle!!.text.toString().trim { it <= ' ' }
            val desc = mAddDesc!!.text.toString().trim { it <= ' ' }
            val from = mWhereFrom!!.text.toString().trim { it <= ' ' }
            val to = mWhereTo!!.text.toString().trim { it <= ' ' }
            val jDate = mJourneyDate!!.text.toString().trim { it <= ' ' }
            val rDate = mReturnDate!!.text.toString().trim { it <= ' ' }
            val totalPerson = mNumberOfPerson!!.text.toString().trim { it <= ' ' }
            val planTripFragmentViewModel =
                ViewModelProvider(this)[PlanTripFragmentViewModel::class.java]
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(from)
                && !TextUtils.isEmpty(to) && !TextUtils.isEmpty(jDate) && !TextUtils.isEmpty(rDate) && !TextUtils.isEmpty(
                    totalPerson
                )
            ) {
                progressBar?.visibility = View.VISIBLE
                var firebaseRepository = FirestoreRepository()
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
                    getDateDiff(jDate, rDate),
                    totalPerson.toLong()
                )
                planTripFragmentViewModel.saveNewTripToFirebase(tripModel)
                findNavController().navigate(R.id.action_addTripFragment_to_planTripFragment)
                progressBar?.visibility = View.INVISIBLE
            }
        }
    }

    private fun getTimestamp(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.timeZone = TimeZone.getDefault()
        return format.format(Date())
    }

    private fun setUpDateDialogs() {
        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
        mAddTitle = myView!!.findViewById(R.id.trip_title)
        mAddDesc = myView!!.findViewById(R.id.trip_desc)
        mWhereFrom = myView!!.findViewById(R.id.place_from)
        mWhereTo = myView!!.findViewById(R.id.place_to)
        mNumberOfPerson = myView!!.findViewById(R.id.total_person)
        mJourneyDate = myView!!.findViewById(R.id.journey_date)
        mReturnDate = myView!!.findViewById(R.id.return_date)
        mAddTripBtn = myView!!.findViewById(R.id.add_trip_btn)
        progressBar = myView!!.findViewById(R.id.progress_horizontal)
    }
}