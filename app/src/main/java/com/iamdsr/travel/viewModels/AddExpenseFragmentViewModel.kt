package com.iamdsr.travel.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.iamdsr.travel.interfaces.MyFirestoreInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.ExpenseModel
import com.iamdsr.travel.repositories.CalculateExpenseFirebaseRepository

class AddExpenseFragmentViewModel: ViewModel(){

    var firebaseRepository = CalculateExpenseFirebaseRepository()
    var savedExpenses : MutableLiveData<List<ExpenseModel>> = MutableLiveData()
    var expenseGroupModel = ExpenseGroupModel()
    var liveDataExpenseGroupModel: MutableLiveData<ExpenseGroupModel?> = MutableLiveData()

    // get member pay status
    fun _getMembersPayStatusLiveDataFromGroup(groupID: String, myFirestoreInterface: MyFirestoreInterface){
        firebaseRepository.getMembersPayStatusFromGroup(groupID)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val model = snapshot.toObject(ExpenseGroupModel::class.java)
                    if (model!=null){
                        liveDataExpenseGroupModel.value = model
                        myFirestoreInterface.onExpenseGroupModelUpdateLiveDataCallback(liveDataExpenseGroupModel)
                    }
                }
            }
    }
    fun _getMembersPayStatusFromGroup(groupID: String, myFirestoreInterface: MyFirestoreInterface){
        firebaseRepository.getMembersPayStatusFromGroup(groupID)
            .get()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val model = task.result.toObject(ExpenseGroupModel::class.java)
                    if (model!=null){
                        myFirestoreInterface.onExpenseGroupModelUpdateCallback(model)
                    }
                }
            }
    }

    // update members payment status
    fun _updateMemberPaymentsToFirebaseFirestore(expenseGroupModel: ExpenseGroupModel,
                                                 addMemberPayStatusMap: MutableMap<String, MutableMap<String, Double>>){

        firebaseRepository.updateMemberPaymentsToFirebaseFirestore(expenseGroupModel).
        set(addMemberPayStatusMap as Map<String, Any>, SetOptions.merge()).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to add user!")
        }
    }

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
                    val source = if (value.metadata.isFromCache)
                        "local cache"
                    else
                        "server"
                    Log.d("TAG", "Data fetched from $source")
                    savedExpenses.value = savedExpList
                })
        return savedExpenses
    }
}