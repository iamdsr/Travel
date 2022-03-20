package com.iamdsr.travel.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.repositories.FirestoreRepository

class ItineraryTimelineViewModel: ViewModel() {

    var firebaseRepository = FirestoreRepository()
    var savedItineraries : MutableLiveData<List<ItineraryModel>> = MutableLiveData()

    // Add new itinerary to Firebase Database
    fun _addNewItineraryToFirebaseFirestore(itineraryModel: ItineraryModel){
        firebaseRepository.addNewItineraryToFirebaseFirestore(itineraryModel).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to save Trip!")
        }
    }
}