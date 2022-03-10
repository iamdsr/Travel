package com.iamdsr.travel.PlanTrip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iamdsr.travel.CustomRecyclerViewAdapters.PlannedTripsRecyclerAdapter;
import com.iamdsr.travel.Interfaces.RecyclerViewActionsInterface;
import com.iamdsr.travel.Models.TripModel;
import com.iamdsr.travel.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class PlanTripFragment extends Fragment implements RecyclerViewActionsInterface {
    //Widgets
    private View view;
    private ExtendedFloatingActionButton mAddTrip;
    private RecyclerView mPlannedTripRecyclerView;
    private PlannedTripsRecyclerAdapter plannedTripsRecyclerAdapter;

    //Utils
    private static final String TAG = "PlanTripFragment";
    private List<TripModel> tripModelList = new ArrayList<TripModel>();

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    public PlanTripFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_plan_trip, container, false);
        setUpWidgets();
        setUpFirebase();
        displayPlannedTrips();
        mAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_planTripFragment_to_addTripFragment);
            }
        });
        return view;
    }

    private void displayPlannedTrips() {
        Log.d(TAG, "displayPlannedTrips: Displaying");
        if (mAuth.getCurrentUser()!=null){
            Query queryToGetAllTrips = firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .orderBy("date_created", Query.Direction.DESCENDING);
            queryToGetAllTrips.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    if (queryDocumentSnapshots!=null){
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                TripModel tripModel = doc.getDocument().toObject(TripModel.class);
                                tripModelList.add(tripModel);
                            }
                        }
                        plannedTripsRecyclerAdapter.submitList(tripModelList);
                    }
                }
            });
        }
    }
    private String getTimestamp(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        format.setTimeZone(TimeZone.getDefault());
        return format.format(new Date());
    }

    private void setUpFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    private void setUpWidgets() {
        mAddTrip = view.findViewById(R.id.add_new_trip);
        mPlannedTripRecyclerView = view.findViewById(R.id.trip_recycler_view);
        mPlannedTripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPlannedTripRecyclerView.setHasFixedSize(true);
        plannedTripsRecyclerAdapter = new PlannedTripsRecyclerAdapter(TripModel.tripModelItemCallback,this);
        mPlannedTripRecyclerView.setAdapter(plannedTripsRecyclerAdapter);
        plannedTripsRecyclerAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @Override
    public void onItemClick(int position) {

    }
}