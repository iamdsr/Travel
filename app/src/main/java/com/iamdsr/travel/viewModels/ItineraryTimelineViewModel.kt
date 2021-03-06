package com.iamdsr.travel.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.repositories.PlanTripsFirestoreRepository

class ItineraryTimelineViewModel: ViewModel() {

    var firebaseRepository = PlanTripsFirestoreRepository()
    var savedItineraries : MutableLiveData<List<ItineraryModel>> = MutableLiveData()

    // Add new itinerary to Firebase Database
    fun _addNewItineraryToFirebaseFirestore(itineraryModel: ItineraryModel){
        firebaseRepository.addNewItineraryToFirebaseFirestore(itineraryModel).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to save Trip!")
        }
    }

    fun updateItinerary(map: MutableMap<String, Any>, itineraryModel: ItineraryModel){
        firebaseRepository.updateItineraryToFirebaseFirestore(map, itineraryModel).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to update Trip!")
        }
    }

    // get realtime updates from firebase regarding saved addresses
    fun _getAllSavedItinerariesFromFirebaseFirestore(tripID: String): LiveData<List<ItineraryModel>> {
        firebaseRepository.getAllSavedItinerariesFromFirebaseFirestore(tripID)
            .orderBy("day", Query.Direction.DESCENDING)
            .orderBy("technical_time", Query.Direction.DESCENDING)
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, e ->
                    if (e != null) {
                        //Log.w(TAG, "Listen failed.", e)
                        //savedTrips.value = null
                        return@EventListener
                    }

                    val savedItineraryList : MutableList<ItineraryModel> = mutableListOf()
                    for (doc in value!!) {
                        val itineraryItem = doc.toObject(ItineraryModel::class.java)
                        savedItineraryList.add(itineraryItem)
                    }
                    savedItineraries.value = savedItineraryList
                })
        return savedItineraries
    }
}