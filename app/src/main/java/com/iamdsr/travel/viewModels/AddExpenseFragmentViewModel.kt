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
import com.iamdsr.travel.models.ExpenseModel
import com.iamdsr.travel.repositories.CalculateExpenseFirebaseRepository

class AddExpenseFragmentViewModel: ViewModel(){

    var firebaseRepository = CalculateExpenseFirebaseRepository()
    var savedExpenses : MutableLiveData<List<ExpenseModel>> = MutableLiveData()

    // Add new Expense to Firebase Database
    fun _addNewExpenseToFirebaseFirestore(expenseModel: ExpenseModel){
        firebaseRepository.addNewExpenseToFirebaseFirestore(expenseModel).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to save Group!")
        }
    }

    fun _getAllSavedExpenseFromFirebaseFirestore(expenseGroupID: String): LiveData<List<ExpenseModel>> {
        firebaseRepository.getAllSavedExpensesFromFirebaseFirestore(expenseGroupID)
            .orderBy("expense_create_date", Query.Direction.DESCENDING)
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, e ->
                    if (e != null) {
                        //Log.w(TAG, "Listen failed.", e)
                        //savedTrips.value = null
                        return@EventListener
                    }

                    val savedExpList : MutableList<ExpenseModel> = mutableListOf()
                    for (doc in value!!) {
                        val expItem = doc.toObject(ExpenseModel::class.java)
                        savedExpList.add(expItem)
                    }
                    savedExpenses.value = savedExpList
                })
        return savedExpenses
    }
}