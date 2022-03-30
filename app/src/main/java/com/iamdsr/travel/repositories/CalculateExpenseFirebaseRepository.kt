package com.iamdsr.travel.repositories

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

    // Expense firebase methods -----------------------------------------------------------------------------------------------------

    fun updateMemberPaymentsToFirebaseFirestore(expenseGroupModel: ExpenseGroupModel) :DocumentReference {
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(expenseGroupModel.id)
        }
        return documentReference!!
    }

    fun getMembersPayStatusFromGroup(groupID: String) : DocumentReference{
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(groupID)
        }
        return documentReference!!
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

    fun getNewExpenseID(expenseGroupId: String): String{
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(expenseGroupId)
                .collection("expenses")
                .document()
        }
        return documentReference!!.id
    }

    fun getAllSavedExpensesFromFirebaseFirestore(expenseGroupId: String): CollectionReference {
        return firebaseFirestore.collection("expense_groups")
            .document(expenseGroupId)
            .collection("expenses")
    }

    // Expense Group firebase methods -----------------------------------------------------------------------------------------------------

    fun addNewExpenseGroupToFirebaseFirestore(expenseGroupModel: ExpenseGroupModel) : Task<Void> {
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document(expenseGroupModel.id)
        }
        return documentReference!!.set(expenseGroupModel)
    }

    fun getNewExpenseGroupID(): String{
        val documentReference = user?.let {
            firebaseFirestore.collection("expense_groups")
                .document()
        }
        return documentReference!!.id
    }

    fun getAllSavedExpenseGroupsFromFirebaseFirestore(): CollectionReference {
        return firebaseFirestore.collection("expense_groups")
    }

    fun getSearchedUsersFromFirebaseFirestore(): CollectionReference {
        return firebaseFirestore.collection("users")
    }

    fun addMemberToExpenseGroupFirebaseFirestore(groupID: String) : DocumentReference {
        return firebaseFirestore.collection("expense_groups").document(groupID)
    }

}