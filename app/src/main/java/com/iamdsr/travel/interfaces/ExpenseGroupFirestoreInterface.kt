package com.iamdsr.travel.interfaces

import androidx.lifecycle.MutableLiveData
import com.iamdsr.travel.models.ExpenseGroupModel

interface ExpenseGroupFirestoreInterface {
    fun onExpenseGroupModelUpdateCallback (model: ExpenseGroupModel)
    fun onExpenseGroupModelUpdateLiveDataCallback (liveData: MutableLiveData<ExpenseGroupModel?>)
}