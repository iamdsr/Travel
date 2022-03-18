package com.iamdsr.travel.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ItinerarySharedViewModel : ViewModel() {
    private var tripID: MutableLiveData<String> = MutableLiveData()

    fun setText(input: String){
        tripID.value = input
    }
    fun getText() : LiveData<String>{
        return tripID
    }
}