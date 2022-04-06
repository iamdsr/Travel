package com.iamdsr.travel.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.*
import com.iamdsr.travel.interfaces.ExpenseManagementFirestoreInterface
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.ExpenseModel
import com.iamdsr.travel.models.UserModel
import com.iamdsr.travel.repositories.CalculateExpenseFirebaseRepository

class ExpenseManagementViewModel: ViewModel(){

    var firebaseRepository = CalculateExpenseFirebaseRepository()
    var savedExpenses : MutableLiveData<List<ExpenseModel>> = MutableLiveData()
    var expenseGroupModel = ExpenseGroupModel()
    private var savedExpenseGroups : MutableLiveData<List<ExpenseGroupModel>> = MutableLiveData()
    private var users : MutableLiveData<List<UserModel>> = MutableLiveData()

    /*
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Manage Expenses View Model methods
     * Used in Fragments ->
     * Contains methods -> 1. addNewExpenseToFirebaseFirestore 2. getAllSavedExpenses 3. deleteExpense
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     */


    fun addNewExpense(expenseModel: ExpenseModel){
        firebaseRepository.addNewExpenseToFirebaseFirestore(expenseModel).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to save new expense to Firestore!")
        }
    }

    fun getAllSavedExpenses(expenseGroupID: String): LiveData<List<ExpenseModel>> {
        firebaseRepository.getAllSavedExpensesFromFirebaseFirestore(expenseGroupID)
            .orderBy("expense_create_date", Query.Direction.DESCENDING)
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, e ->
                    if (e != null)
                        return@EventListener
                    val savedExpList : MutableList<ExpenseModel> = mutableListOf()
                    for (doc in value!!) {
                        val expItem = doc.toObject(ExpenseModel::class.java)
                        savedExpList.add(expItem)
                    }
                    savedExpenses.value = savedExpList
                })
        return savedExpenses
    }

    fun deleteExpense(item: ExpenseModel){
        firebaseRepository.deleteExpense(item).addOnFailureListener {
            Log.e("TAG","Failed to delete Expense")
        }
    }

    /*
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Manage Expenses Groups View Model methods
     * Used in Fragments ->
     * Contains methods -> 1. addNewExpenseGroup 2. getAllSavedExpenseGroups 3. updateTotalIndividualMemberExpenses
     * 4. updateMemberPaymentsStatusInGroups 5. getMemberPaymentsStatusInGroups
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    fun addNewExpenseGroup(expenseGroupModel: ExpenseGroupModel){
        firebaseRepository.addNewExpenseGroupToFirebaseFirestore(expenseGroupModel).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to save Expense Group!")
        }
    }

    fun getAllSavedExpenseGroups(userName: String): LiveData<List<ExpenseGroupModel>> {
        firebaseRepository.getAllSavedExpenseGroupsFromFirebaseFirestore()
            .whereArrayContains("members_name",userName)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, e ->
                    if (e != null)
                        return@EventListener
                    val savedExpGrpList : MutableList<ExpenseGroupModel> = mutableListOf()
                    for (doc in value!!) {
                        val expGrpItem = doc.toObject(ExpenseGroupModel::class.java)
                        savedExpGrpList.add(expGrpItem)
                    }
                    savedExpenseGroups.value = savedExpGrpList
                })
        return savedExpenseGroups
    }

    fun updateTotalIndividualMemberExpenses(expenseGroupModel: ExpenseGroupModel,
                                            memberExpensesMap: MutableMap<String, MutableMap<String, Double>>){

        firebaseRepository.updateMemberPaymentsStatusInGroupsToFirebaseFirestore(expenseGroupModel).
        set(memberExpensesMap as Map<String, Any>, SetOptions.merge()).addOnFailureListener {
            Log.e("TAG","Failed to update Total Individual Member Expenses !")
        }
    }

    fun updateMemberPaymentsStatusInGroups(expenseGroupModel: ExpenseGroupModel,
                                           addMemberPayStatusMap: MutableMap<String, MutableMap<String, Double>>){

        firebaseRepository.updateMemberPaymentsStatusInGroupsToFirebaseFirestore(expenseGroupModel).
        set(addMemberPayStatusMap as Map<String, Any>, SetOptions.merge()).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to add user!")
        }
    }

    fun getMemberPaymentsStatusInGroupsOnce(groupID: String, expenseManagementFirestoreInterface: ExpenseManagementFirestoreInterface){
        firebaseRepository.getMemberPaymentsStatusInGroupsFromFirebaseFirestore(groupID)
            .get().addOnSuccessListener { document ->
            if (document != null) {
                val model = document.toObject(ExpenseGroupModel::class.java)
                if (model != null) {
                    expenseManagementFirestoreInterface.onExpenseGroupModelUpdateCallback(model)
                }
            } else {
                Log.d("TAG", "No such document")
            }
        }
    }

    fun getMemberPaymentsStatusInGroupsRT(groupID: String, expenseManagementFirestoreInterface: ExpenseManagementFirestoreInterface){
        firebaseRepository.getMemberPaymentsStatusInGroupsFromFirebaseFirestore(groupID)
            .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val model = snapshot.toObject(ExpenseGroupModel::class.java)
                if (model != null) {
                    expenseManagementFirestoreInterface.onExpenseGroupModelUpdateCallback(model)
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    /*
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Search member to add in groups View Model methods
     * Used in Fragments ->
     * Contains methods -> 1. getSearchedUsers 2. addAndUpdateSearchedMemberToExpenseGroup
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    fun getSearchedUsers(searchText: String): LiveData<List<UserModel>>{
        Log.d("TAG", "_getSearchedUsersFromFirebaseFirestore: $searchText")
        firebaseRepository.getSearchedUsersFromFirebaseFirestore()
            .orderBy("full_name_lowercase")
            .startAt(searchText)
            .endAt(searchText+'\uf8ff')
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, e ->
                    if (e != null)
                        return@EventListener
                    val userList : MutableList<UserModel> = mutableListOf()
                    for (doc in value!!) {
                        val usersItem = doc.toObject(UserModel::class.java)
                        userList.add(usersItem)
                    }
                    users.value = userList
                })
        return users
    }

    fun addAndUpdateSearchedMemberToExpenseGroup(groupID: String,
                                        memberListMap: MutableMap<String, Any>,
                                        addIDMemberMap: MutableMap<String, MutableMap<String, Any>>,
                                        addMemberPayStatusMap: MutableMap<String, MutableMap<String, Double>>,
                                        memberExpenseMap: MutableMap<String, MutableMap<String, Double>>){

        firebaseRepository.addAndUpdateSearchedMemberToExpenseGroupFirebaseFirestore(groupID).update(memberListMap).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to add user!")
        }
        firebaseRepository.addAndUpdateSearchedMemberToExpenseGroupFirebaseFirestore(groupID).set(addIDMemberMap as Map<String, Any>, SetOptions.merge()).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to add user!")
        }
        firebaseRepository.addAndUpdateSearchedMemberToExpenseGroupFirebaseFirestore(groupID).set(addMemberPayStatusMap as Map<String, Any>, SetOptions.merge()).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to add user!")
        }
        firebaseRepository.addAndUpdateSearchedMemberToExpenseGroupFirebaseFirestore(groupID).set(memberExpenseMap as Map<String, Any>, SetOptions.merge()).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to add user!")
        }
    }
}