package com.iamdsr.travel.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.iamdsr.travel.models.UserModel

class UserFireStoreRepository {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    fun updateUserProfileImage(userID: String) : DocumentReference {
        val documentReference = user?.let {
            firebaseFirestore.collection("users")
                .document(userID)
        }
        return documentReference!!
    }

    fun getUserDetails(userID: String) : DocumentReference{
        val documentReference = user?.let {
            firebaseFirestore.collection("users")
                .document(userID)
        }
        return documentReference!!
    }
}