package com.iamdsr.travel.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.iamdsr.travel.models.ExpenseGroupModel
import com.iamdsr.travel.models.ExpenseModel

class CalculateExpenseFirebaseRepository {
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    /*
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Manage Expenses Repositories methods
     * Used in View Models -> ExpenseManagementViewModel.kt
     * Contains methods -> 1. getNewExpenseID 2. addNewExpenseToFirebaseFirestore 3. getAllSavedExpensesFromFirebaseFirestore 4. deleteExpense
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    fun getNewExpenseID(expenseGroupId: String): String{
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(expenseGroupId)
                .collection("expenses")
                .document()
        }
        return documentReference!!.id
    }

    fun addNewExpenseToFirebaseFirestore(expenseModel: ExpenseModel) : Task<Void> {
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(expenseModel.group_id)
                .collection("expenses")
                .document(expenseModel.id)
        }
        return documentReference!!.set(expenseModel)
    }

    fun getAllSavedExpensesFromFirebaseFirestore(expenseGroupId: String): CollectionReference {
        Log.d("TAG", "getAllSavedExpensesFromFirebaseFirestore: Firestore called")
        return firebaseFirestore.collection("expense_groups")
            .document(expenseGroupId)
            .collection("expenses")
    }

    fun deleteExpense(item: ExpenseModel): Task<Void> {
        val documentReference =  firebaseFirestore
            .collection("expense_groups")
            .document(item.group_id)
            .collection("expenses")
            .document(item.id)

        return documentReference.delete()
    }

    /*
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Manage Expense Groups Repositories methods
     * Used in View Models -> ExpenseManagementViewModel.kt
     * Contains methods -> 1. getNewExpenseGroupID 2. getAllSavedExpenseGroupsFromFirebaseFirestore 3. updateMemberPaymentsToFirebaseFirestore
     * 4. getMembersPayStatusFromGroup 5. addNewExpenseGroupToFirebaseFirestore
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    fun getNewExpenseGroupID(): String{
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document()
        }
        return documentReference!!.id
    }

    fun addNewExpenseGroupToFirebaseFirestore(expenseGroupModel: ExpenseGroupModel) : Task<Void> {
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(expenseGroupModel.id)
        }
        return documentReference!!.set(expenseGroupModel)
    }

    fun getAllSavedExpenseGroupsFromFirebaseFirestore(): CollectionReference {
        return firebaseFirestore.collection("expense_groups")
    }

    fun updateMemberPaymentsStatusInGroupsToFirebaseFirestore(expenseGroupModel: ExpenseGroupModel) :DocumentReference {
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(expenseGroupModel.id)
        }
        return documentReference!!
    }

    fun getMemberPaymentsStatusInGroupsFromFirebaseFirestore(groupID: String) : DocumentReference{
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(groupID)
        }
        return documentReference!!
    }

    /*
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Manage Search user to add in expense group Repositories methods
     * Used in Fragments -> ExpenseManagementViewModel.kt
     * Contains methods -> 1. getSearchedUsersFromFirebaseFirestore 2. addMemberToExpenseGroupFirebaseFirestore
     * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
     */

    fun getSearchedUsersFromFirebaseFirestore(): CollectionReference {
        return firebaseFirestore.collection("users")
    }

    fun addAndUpdateSearchedMemberToExpenseGroupFirebaseFirestore(groupID: String) : DocumentReference {
        return firebaseFirestore.collection("expense_groups").document(groupID)
    }

}