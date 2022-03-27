package com.iamdsr.travel.viewModels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.iamdsr.travel.models.UserModel
import com.iamdsr.travel.repositories.FirestoreRepository

class SearchMemberFragmentViewModel: ViewModel() {

    var firebaseRepository = FirestoreRepository()
    var users : MutableLiveData<List<UserModel>> = MutableLiveData()

    fun _addMemberToExpenseGroupFirebaseFirestore(groupID: String, expenseGroupMap: MutableMap<String, Any>){
        firebaseRepository.addMemberToExpenseGroupFirebaseFirestore(groupID).update(expenseGroupMap).addOnFailureListener {
            Log.e(ContentValues.TAG,"Failed to add user!")
        }
    }

    fun _getSearchedUsersFromFirebaseFirestore(searchText: String): LiveData<List<UserModel>>{
        Log.d("TAG", "_getSearchedUsersFromFirebaseFirestore: $searchText")
        firebaseRepository.getSearchedUsersFromFirebaseFirestore()
            .orderBy("full_name_lowercase")
            .startAt(searchText)
            .endAt(searchText+'\uf8ff')
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, e ->
                    if (e != null) {
                        return@EventListener
                    }
                    val userList : MutableList<UserModel> = mutableListOf()
                    for (doc in value!!) {
                        val usersItem = doc.toObject(UserModel::class.java)
                        Log.d("TAG", "_getSearchedUsersFromFirebaseFirestore: $usersItem")
                        userList.add(usersItem)
                    }
                    users.value = userList
                })
        return users
    }
}