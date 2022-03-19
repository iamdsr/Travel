package com.iamdsr.travel.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.repositories.FirestoreRepository

class ItinerarySharedViewModel : ViewModel() {

    var firebaseRepository = FirestoreRepository()
    private var tripID: MutableLiveData<String> = MutableLiveData()
    private var tripModel: MutableLiveData<TripModel> = MutableLiveData()

    fun _getSingleTripFromFirestoreDatabase(tripId: String): LiveData<TripModel>{
        if (firebaseRepository.getSingleTripFromFirestoreDatabase(tripId)!=null){
            firebaseRepository.getSingleTripFromFirestoreDatabase(tripId)!!
                .addOnFailureListener { exception -> Log.d("TAG", "get failed with ", exception) }
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot!=null){
                        tripModel.value = documentSnapshot.toObject(TripModel::class.java)
                    }
                }
        }
        return tripModel
    }

    fun setText(input: String){
        tripID.value = input
    }
    fun getText() : LiveData<String>{
        return tripID
    }
}