package com.iamdsr.travel.models

data class ExpenseGroupModel(
    var id: String,
    var name: String,
    var group_image_url: String,
    var member_count: Long,
    var created_by_id: String,
    var members: ArrayList<String>
) {
    constructor() : this("", "", "", 0, "", arrayListOf())

    override fun toString(): String {
        return "ExpenseGroupModel(id='$id', name='$name', group_image_url='$group_image_url', " +
                "member_count=$member_count, created_by_id='$created_by_id', members=$members)"
    }

}