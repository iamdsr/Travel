package com.iamdsr.travel.planTrip;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iamdsr.travel.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class UpdateTripFragment extends Fragment {

    //widgets
    private View view;
    private EditText mJourneyDate, mReturnDate, mAddTitle, mAddDesc, mWhereFrom, mWhereTo, mNumberOfPerson;
    private Button mUpdateTripBtn;
    private EditText clickedEditText;
    private ProgressDialog progressDialog;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    //Utils
    private String currUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "AddTripFragment";
    final Calendar myCalendar= Calendar.getInstance();
    private String receivedDocID="";
    //private PlanTripFragmentViewModel planTripFragmentViewModel;

    public UpdateTripFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_trip, container, false);
        setUpWidgets();
        setupFirebase();
        setUpDateDialogs();

        mUpdateTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTripToDB();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments()!=null){
            receivedDocID = getArguments().getString("TRIP_ID");
            mAddTitle.setText(getArguments().getString("TRIP_TITLE"));
            mAddDesc.setText(getArguments().getString("TRIP_DESCRIPTION"));
            mJourneyDate.setText(getArguments().getString("JOURNEY_DATE"));
            mReturnDate.setText(getArguments().getString("RETURN_DATE"));
            mWhereFrom.setText(getArguments().getString("PLACE_FROM"));
            mWhereTo.setText(getArguments().getString("PLACE_TO"));
            mNumberOfPerson.setText(String.valueOf(getArguments().getLong("TOTAL_PAX")));
        }
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

    private void updateTripToDB(){
        String title = mAddTitle.getText().toString().trim();
        String desc = mAddDesc.getText().toString().trim();
        String from = mWhereFrom.getText().toString().trim();
        String to = mWhereTo.getText().toString().trim();
        String jDate = mJourneyDate.getText().toString().trim();
        String rDate = mReturnDate.getText().toString().trim();
        String totalPerson = mNumberOfPerson.getText().toString().trim();
        //DocumentReference ref = firebaseFirestore.collection("trips").document();
        //final String newTripID = ref.getId();
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(from)
                && !TextUtils.isEmpty(to) && !TextUtils.isEmpty(jDate) && !TextUtils.isEmpty(rDate) && !TextUtils.isEmpty(totalPerson)){
            firebaseFirestore.collection("users")
                    .document(currUserID)
                    .collection("trips")
                    .document(receivedDocID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null){
//                                TripModel model = task.getResult().toObject(TripModel.class);
//                                if (model!=null){
//                                    if (!model.getTrip_title().equals(title)){
//                                        updateTrip(title,null,null,null,null,null,-1);
//                                    }
//                                    if (!model.getTrip_desc().equals(desc)){
//                                        updateTrip(null,desc,null,null,null,null,-1);
//                                    }
//                                    if (!model.getPlace_from().equals(from)){
//                                        updateTrip(null,null,null,null,from,null,-1);
//                                    }
//                                    if (!model.getPlace_to().equals(to)){
//                                        updateTrip(null,null,null,null,null,to,-1);
//                                    }
//                                    if (!model.getJourney_date().equals(jDate) || !model.getReturn_date().equals(rDate)){
//                                        updateTrip(null,null,jDate,rDate,null,null,-1);
//                                    }
//                                    if (model.getTotal_heads() != Long.parseLong(totalPerson)){
//                                        updateTrip(null,null,null,null,null,null,Long.parseLong(totalPerson));
//                                    }
//                                }
                            }
                        }
                    });
        }
    }
    public void updateTrip(String title, String desc, String jDate, String rDate, String from, String to, long pax){

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating Trip");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        if (title != null) {
            Map<String , Object> tripMap = new HashMap<>();
            tripMap.put("trip_title", title);
            firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .document(receivedDocID)
                    .update(tripMap);
        }
        if (desc != null) {
            Map<String , Object> tripMap = new HashMap<>();
            tripMap.put("trip_desc", desc);
            firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .document(receivedDocID)
                    .update(tripMap);
        }
        if (jDate != null && rDate != null) {
            Map<String , Object> tripMap = new HashMap<>();
            tripMap.put("journey_date", jDate);
            tripMap.put("return_date", rDate);
            tripMap.put("duration_in_days", getDateDiff(jDate,rDate));
            firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .document(receivedDocID)
                    .update(tripMap);
        }
        if (from != null) {
            Map<String , Object> tripMap = new HashMap<>();
            tripMap.put("place_from", from);
            firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .document(receivedDocID)
                    .update(tripMap);
        }
        if (to != null) {
            Map<String , Object> tripMap = new HashMap<>();
            tripMap.put("place_to", to);
            firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .document(receivedDocID)
                    .update(tripMap);
        }
        if (pax != -1) {
            Map<String , Object> tripMap = new HashMap<>();
            tripMap.put("total_heads", pax);
            firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .document(receivedDocID)
                    .update(tripMap);
        }
//        planTripFragmentViewModel = new ViewModelProvider(getActivity()).get(PlanTripFragmentViewModel.class);
//        planTripFragmentViewModel.updateTrips((List<TripModel>) planTripFragmentViewModel.getPlannedTripListMutableLiveData());
        progressDialog.dismiss();
        Toast.makeText(getContext(),"Congrats! Trip Update Successfully.",Toast.LENGTH_SHORT).show();
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
        mUpdateTripBtn = view.findViewById(R.id.update_trip_btn);
    }
}