package com.iamdsr.travel.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
}