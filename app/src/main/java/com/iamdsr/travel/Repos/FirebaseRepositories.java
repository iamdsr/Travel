package com.iamdsr.travel.Repos;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.iamdsr.travel.Interfaces.FirestoreTaskCompleteInterface;
import com.iamdsr.travel.Models.TripModel;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepositories {

    private FirestoreTaskCompleteInterface firestoreTaskCompleteInterface;
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private List<TripModel> tripModelList = new ArrayList<>();
    //private Query query = firebaseFirestore.collection("users").document(mAuth.getCurrentUser().getUid()).collection("trips").orderBy("date_created", Query.Direction.DESCENDING);

    public FirebaseRepositories(FirestoreTaskCompleteInterface firestoreTaskCompleteInterface){
        this.firestoreTaskCompleteInterface = firestoreTaskCompleteInterface;
    }
    public void getUpdatedPlannedTripsFromDB(List<TripModel> oldLList){
        if (mAuth.getCurrentUser()!=null){
            List<TripModel> newLList = new ArrayList<>(oldLList);
            //Log.d("TAG", "getPlannedTripsFromDB: "+mAuth.getCurrentUser().getUid());
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
                            if (doc.getType() == DocumentChange.Type.MODIFIED){
                                Log.d("TAG", "----------------------------------------- MODIFIED -------------------------------------------------------");
                                Log.d("TAG", "onEvent: MODIFIED CALLED "+doc.getDocument().toObject(TripModel.class));
                                TripModel tripModel = doc.getDocument().toObject(TripModel.class);
                                if (doc.getOldIndex() == doc.getNewIndex()) {
                                    // Item changed but remained in same position
                                    newLList.set(doc.getOldIndex(),tripModel);
                                }
                                firestoreTaskCompleteInterface.onPlanUpdated(newLList);
                            }
                        }
                        /*for (int i = 0; i < newLList.size(); i++) {
                            Log.d("TAG", "onEvent REPO INSIDE FOR: "+tripModelList.get(i).getTrip_title());
                        }*/
                    }
                }
            });
        }

    }
    public void getPlannedTripsFromDB(){
        Log.d("TAG", "getPlannedTripsFromDB: Called ");
        if (mAuth.getCurrentUser()!=null){
            //Log.d("TAG", "getPlannedTripsFromDB: "+mAuth.getCurrentUser().getUid());
            Query queryToGetAllTrips = firebaseFirestore
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .collection("trips")
                    .orderBy("date_created", Query.Direction.ASCENDING);
            queryToGetAllTrips.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    if (queryDocumentSnapshots!=null){
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if (doc.getType() == DocumentChange.Type.ADDED){
                                Log.d("TAG", "----------------------------------------- ON ADDED -------------------------------------------------------");
                                Log.d("TAG", "onEvent: ADD CALLED "+doc.getDocument().get("trip_title"));
                                TripModel tripModel = doc.getDocument().toObject(TripModel.class);
                                if (tripModelList.size()>0){
                                    tripModelList.add(0,tripModel);
                                }
                                else {
                                    tripModelList.add(tripModel);
                                }
                                firestoreTaskCompleteInterface.onNewPlansAdded(tripModelList);
                            }
                        }
                        /*for (int i = 0; i < tripModelList.size(); i++) {
                            Log.d("TAG", "onEvent REPO INSIDE FOR: "+tripModelList.get(i).getTrip_title());
                        }*/
                    }
                }
            });
        }
    }
}