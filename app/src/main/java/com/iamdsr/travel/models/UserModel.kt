package com.iamdsr.travel.models;


data class UserModel(
    var id: String,
    var full_name: String,
    var username: String,
    var email: String,
    var full_name_lowercase: String,
    var user_profile_image_url: String
) {
    constructor(): this("","","","","", "")

}
