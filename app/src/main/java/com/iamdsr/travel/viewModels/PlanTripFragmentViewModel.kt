package com.iamdsr.travel.viewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.repositories.PlanTripsFirestoreRepository

class PlanTripFragmentViewModel : ViewModel() {

    var firebaseRepository = PlanTripsFirestoreRepository()
    var savedTrips : MutableLiveData<List<TripModel>> = MutableLiveData()

    // Add new trip to Firebase Database
    fun _addNewTripToFirebaseFirestore(tripModel: TripModel){
        firebaseRepository.addNewTripToFirebaseFirestore(tripModel).addOnFailureListener {
            Log.e(TAG,"Failed to save Trip!")
        }
    }

    // Update trip
    fun _updateTripToFirebaseFirestore(tripMap: MutableMap<String, Any>, tripID: String){
        firebaseRepository.updateTripToFirebaseFirestore(tripMap, tripID).addOnFailureListener {
            Log.e(TAG,"Failed to update Trip!")
        }
    }

    // get realtime updates from firebase regarding saved addresses
    fun _getAllSavedTripFromFirebaseFirestore(): LiveData<List<TripModel>> {
        firebaseRepository.getAllSavedTripFromFirebaseFirestore().orderBy("date_created", Query.Direction.DESCENDING).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                //Log.w(TAG, "Listen failed.", e)
                //savedTrips.value = null
                return@EventListener
            }

            val savedTripsList : MutableList<TripModel> = mutableListOf()
            for (doc in value!!) {
                val plannedTripItem = doc.toObject(TripModel::class.java)
                savedTripsList.add(plannedTripItem)
            }
            savedTrips.value = savedTripsList
        })
        return savedTrips
    }
}