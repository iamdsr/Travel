package com.iamdsr.travel.models

data class ExpenseModel(
    var id: String,
    var name: String,
    var amount_paid: Double,
    var paid_by_name: String,
    var amount_lent: Double,
    var amount_borrowed: Double,
    var split_mode: String,
    var split_among_members: MutableMap<String, Any>,
    var split_among_members_count: Int,
    var expense_create_date: String,
    var expense_update_date: String,
    var expense_calculation_map : MutableMap<String, String>,
    var update_comments: MutableMap<String, Any>,
    var paid_by_ID: String,
    var group_name: String,
    var group_id: String
) {
    constructor(): this("", "" ,0.0 ,
        "" ,0.0,0.0, "" ,
        mutableMapOf(), 0 ,"" ,"",
        mutableMapOf(), mutableMapOf(), "", "", "")

}