package com.iamdsr.travel.interfaces
import com.iamdsr.travel.models.UserModel

interface UsersFirestoreInterface {
    fun onUserDataAdded (model: UserModel)
    fun onUserDataUpdated (model: UserModel)
}