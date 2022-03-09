package com.iamdsr.travel.PlanTrip;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamdsr.travel.AppLaunchSetup.LoginActivity;
import com.iamdsr.travel.Models.TripModel;
import com.iamdsr.travel.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AddTripFragment extends Fragment {

    //widgets
    private View view;
    private EditText mJourneyDate, mReturnDate, mAddTitle, mAddDesc, mWhereFrom, mWhereTo, mNumberOfPerson;
    private Button mAddTripBtn;
    private EditText clickedEditText;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    //Utils
    private String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "AddTripFragment";
    final Calendar myCalendar= Calendar.getInstance();

    public AddTripFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        setUpWidgets();
        setupFirebase();
        setUpDateDialogs();

        mAddTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTripToDB();
            }
        });


        return view;
    }

    private void setUpDateDialogs(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(clickedEditText);
            }
        };
        mJourneyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedEditText = (EditText) view;
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mReturnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedEditText = (EditText) view;
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void addTripToDB(){
        String title = mAddTitle.getText().toString().trim();
        String desc = mAddDesc.getText().toString().trim();
        String from = mWhereFrom.getText().toString().trim();
        String to = mWhereTo.getText().toString().trim();
        String jDate = mJourneyDate.getText().toString().trim();
        String rDate = mReturnDate.getText().toString().trim();
        String totalPerson = mNumberOfPerson.getText().toString().trim();
        DocumentReference ref = firebaseFirestore.collection("trips").document();
        final String newTripID = ref.getId();
        getDateDiff(jDate,rDate);
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(from)
                && !TextUtils.isEmpty(to) && !TextUtils.isEmpty(jDate) && !TextUtils.isEmpty(rDate) && !TextUtils.isEmpty(totalPerson)){
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Adding Trip");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            TripModel tripModel = new TripModel(newTripID,title,desc,jDate,rDate,from,to,currUserID,getDateDiff(jDate,rDate),Long.parseLong(totalPerson),getTimestamp());
            firebaseFirestore.collection("users")
                    .document(currUserID)
                    .collection("trips")
                    .document(newTripID)
                    .set(tripModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    mAddTitle.setText(null);
                    mAddDesc.setText(null);
                    mWhereFrom.setText(null);
                    mWhereTo.setText(null);
                    mJourneyDate.setText(null);
                    mReturnDate.setText(null);
                    mNumberOfPerson.setText(null);
                    Navigation.findNavController(view).navigate(R.id.action_addTripFragment_to_planTripFragment);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        else{

        }
    }

    private void updateLabel(EditText view) {
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.getDefault());
        view.setText(dateFormat.format(myCalendar.getTime()));
    }

    private long getDateDiff(String CurrentDate, String FinalDate){
        try {
            Date date1;
            Date date2;
            String myFormat="dd/MM/yyyy";
            SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.getDefault());
            date1 = dateFormat.parse(FinalDate);
            date2 = dateFormat.parse(CurrentDate);
            long difference = Math.abs(date1.getTime() - date2.getTime());
            Log.d(TAG, "getDateDiff: currdate : "+date1);
            Log.d(TAG, "getDateDiff: enddate : "+date2);
            Log.d(TAG, "getDateDiff: difference : "+difference/(24 * 60 * 60 * 1000));
            return difference / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            Log.d(TAG, "getDateDiff: "+e.getMessage());
            return 0;
        }
    }

    private String getTimestamp(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        format.setTimeZone(TimeZone.getDefault());
        return format.format(new Date());
    }

    private void setupFirebase(){
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setUpWidgets() {
        mAddTitle = view.findViewById(R.id.trip_title);
        mAddDesc = view.findViewById(R.id.trip_desc);
        mWhereFrom = view.findViewById(R.id.place_from);
        mWhereTo = view.findViewById(R.id.place_to);
        mNumberOfPerson = view.findViewById(R.id.total_person);
        mJourneyDate = view.findViewById(R.id.journey_date);
        mReturnDate = view.findViewById(R.id.return_date);
        mAddTripBtn = view.findViewById(R.id.add_trip_btn);
    }
}