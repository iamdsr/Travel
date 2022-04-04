package com.iamdsr.travel.viewModels
import android.util.Log
import androidx.lifecycle.ViewModel
import com.iamdsr.travel.interfaces.UsersFirestoreInterface
import com.iamdsr.travel.models.UserModel
import com.iamdsr.travel.repositories.UserFireStoreRepository

class UserProfileViewModel: ViewModel() {

    var firebaseRepository = UserFireStoreRepository()
    //var savedItineraries : MutableLiveData<List<ItineraryModel>> = MutableLiveData()

    fun _updateUserProfileImage(userID: String, map: Map<String, String>){
        firebaseRepository.updateUserProfileImage(userID).update(map)
            .addOnFailureListener { Log.d("TAG", "_updateUserProfileImage: Failed to update") }
    }

    fun _getUserDetails(userID: String, userModelInterface: UsersFirestoreInterface){
        firebaseRepository.getUserDetails(userID)
            .addSnapshotListener{ snapshot, e ->
                if (e!=null){
                    return@addSnapshotListener
                }
                else{
                    if (snapshot != null && snapshot.exists()) {
                        val model = snapshot.toObject(UserModel::class.java)
                        if (model!=null){
                            userModelInterface.onUserDataUpdated(model)
                        }
                    }
                }
            }
    }
}