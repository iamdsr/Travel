package com.iamdsr.travel.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.iamdsr.travel.models.TripModel
import com.iamdsr.travel.repositories.FirestoreRepository

class PlanTripFragmentViewModel : ViewModel() {

    var firebaseRepository = FirestoreRepository()
    var savedTrips : MutableLiveData<List<TripModel>> = MutableLiveData()

    // save address to firebase
    fun saveNewTripToFirebase(tripModel: TripModel){
        firebaseRepository.addNewPlannedTripToDB(tripModel).addOnFailureListener {
            //Log.e(TAG,"Failed to save Address!")
        }
    }

    // get realtime updates from firebase regarding saved addresses
    fun getSavedTrips(): LiveData<List<TripModel>> {
        firebaseRepository.getSavedTrips().orderBy("date_created", Query.Direction.DESCENDING).addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
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