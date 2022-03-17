package com.iamdsr.travel.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.iamdsr.travel.models.TripModel
import javax.sql.StatementEvent

class FirestoreRepository {
    val TAG = "FIREBASE_REPOSITORY"
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    // save new trip to firebase
    fun addNewTripToFirebaseFirestore(tripModel: TripModel) : Task<Void>{
        val documentReference = user?.let {
            firebaseFirestore.collection("users")
                .document(it.uid)
                .collection("trips")
                .document()
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
}