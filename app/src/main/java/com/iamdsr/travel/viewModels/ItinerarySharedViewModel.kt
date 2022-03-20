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
    private var mutableTripModel: MutableLiveData<TripModel> = MutableLiveData()


    fun setModel(model: TripModel){
        mutableTripModel.value = model
    }
    fun getModel() : LiveData<TripModel>{
        return mutableTripModel
    }
}