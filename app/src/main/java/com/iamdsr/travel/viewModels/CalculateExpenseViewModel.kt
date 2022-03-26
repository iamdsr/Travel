package com.iamdsr.travel.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.ItineraryModel
import com.iamdsr.travel.repositories.FirestoreRepository

class CalculateExpenseViewModel: ViewModel()  {

    var firebaseRepository = FirestoreRepository()
    var savedExpenseGroups : MutableLiveData<List<ExpenseGroupModel>> = MutableLiveData()

    // Add new Expense group to Firebase Database
    fun _addNewExpenseGroupToFirebaseFirestore(expenseGroupModel: ExpenseGroupModel){
        firebaseRepository.addNewExpenseGroupToFirebaseFirestore(expenseGroupModel).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to save Group!")
        }
    }

    fun _getAllSavedExpenseGroupsFromFirebaseFirestore(userName: String): LiveData<List<ExpenseGroupModel>> {
        firebaseRepository.getAllSavedExpenseGroupsFromFirebaseFirestore()
            .whereArrayContains("members",userName)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, e ->
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e)
                    //savedTrips.value = null
                    return@EventListener
                }

                val savedExpGrpList : MutableList<ExpenseGroupModel> = mutableListOf()
                for (doc in value!!) {
                    val expGrpItem = doc.toObject(ExpenseGroupModel::class.java)
                    savedExpGrpList.add(expGrpItem)
                }
                    savedExpenseGroups.value = savedExpGrpList
            })
        return savedExpenseGroups
    }
}