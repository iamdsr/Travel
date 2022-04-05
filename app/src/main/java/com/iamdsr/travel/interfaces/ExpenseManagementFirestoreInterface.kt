package com.iamdsr.travel.interfaces

import androidx.lifecycle.MutableLiveData
import com.iamdsr.travel.models.ExpenseGroupModel

interface ExpenseManagementFirestoreInterface {
    fun onExpenseGroupModelUpdateCallback (model: ExpenseGroupModel)
}