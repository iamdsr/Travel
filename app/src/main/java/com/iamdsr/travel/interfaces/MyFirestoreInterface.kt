package com.iamdsr.travel.interfaces

import com.iamdsr.travel.models.ExpenseGroupModel

interface MyFirestoreInterface {
    fun onExpenseGroupModelUpdateCallback (model: ExpenseGroupModel)
}