package com.iamdsr.travel.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.models.TripModel


class FirestoreRepository {
    val TAG = "FIREBASE_REPOSITORY"
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

// ITINERARIES --------------------------------------------------------------------------------------------------------------------------------------
    // save new itinerary to firebase
    fun addNewItineraryToFirebaseFirestore(itineraryModel: ItineraryModel) : Task<Void>{
        val documentReference = user?.let {
            firebaseFirestore.collection("users")
                .document(it.uid)
                .collection("trips")
                .document(itineraryModel.trip_id)
                .collection("itineraries")
                .document(itineraryModel.itinerary_id)
        }
        return documentReference!!.set(itineraryModel)
    }


    fun getNewItineraryID(itineraryID: String): String{
        val documentReference = user?.let {
            firebaseFirestore.collection("users")
                .document(it.uid)
                .collection("trips")
                .document(itineraryID)
                .collection("itineraries")
                .document()
        }
        return documentReference!!.id
    }

    fun getAllSavedItinerariesFromFirebaseFirestore(tripID: String): CollectionReference {
        return firebaseFirestore.collection("users")
            .document(user!!.uid)
            .collection("trips")
            .document(tripID)
            .collection("itineraries")
    }


    // TRIPS --------------------------------------------------------------------------------------------------------------------------------------------
    // save new trip to firebase
    fun addNewTripToFirebaseFirestore(tripModel: TripModel) : Task<Void>{
        val documentReference = user?.let {
            firebaseFirestore.collection("users")
                .document(it.uid)
                .collection("trips")
                .document(tripModel.trip_id)
        }
        return documentReference!!.set(tripModel)
    }

    // Update trip to DB
    fun updateTripToFirebaseFirestore(tripMap: MutableMap<String, Any>, tripID: String) : Task<Void>{
        Log.d(TAG, "updateExistingTripToDB: Trip ID : $tripID")
        Log.d(TAG, "updateExistingTripToDB: Trip ID : $tripMap")
        val documentRef = firebaseFirestore.collection("users").document(user!!.uid).collection("trips").document(tripID)
        return documentRef.update(tripMap)
    }

    fun getNewTripID(): String{
        val documentReference = user?.let {
            firebaseFirestore.collection("users")
                .document(it.uid)
                .collection("trips")
                .document()
        }
        return documentReference!!.id
    }

    // get saved addresses from firebase
    fun getAllSavedTripFromFirebaseFirestore(): CollectionReference {
        return firebaseFirestore.collection("users/${user?.uid}/trips")
    }

    fun updateItineraryToFirebaseFirestore(map: MutableMap<String, Any>, itineraryModel: ItineraryModel): Task<Void> {
        val documentRef = firebaseFirestore
            .collection("users")
            .document(user!!.uid)
            .collection("trips")
            .document(itineraryModel.trip_id)
            .collection("itineraries")
            .document(itineraryModel.itinerary_id)
        return documentRef.update(map)
    }
}