package com.iamdsr.travel.models

data class ExpenseGroupModel(
    var id: String,
    var name: String,
    var group_image_url: String,
    var member_count: Long,
    var created_by_id: String,
    var members_name: ArrayList<String>,
    var members_id_name_map: MutableMap<String, Any>,
    var members_payment_status: MutableMap<String, Any>,
    var timestamp: String
) {
    constructor() : this("", "", "", 0, "", arrayListOf(), mutableMapOf(), mutableMapOf(),"")

    override fun toString(): String {
        return "ExpenseGroupModel(id='$id', name='$name', group_image_url='$group_image_url', member_count=$member_count, created_by_id='$created_by_id', members_name=$members_name, members_id_name_map=$members_id_name_map, members_payment_status=$members_payment_status, timestamp='$timestamp')"
    }


}