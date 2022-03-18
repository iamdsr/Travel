package com.iamdsr.travel.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItinerarySharedViewModel : ViewModel() {
    var tripID = MutableLiveData<String>()

    private fun setText(input: String){
        tripID.value = input
    }
    public fun getText() : LiveData<String>{
        return tripID
    }
}