package com.iamdsr.travel.models

import java.io.Serializable

data class PostsModel(
    val id: String,
    val title: String,
    val description: String,
    val hashtags: ArrayList<String>,
    val timestamp: String,
    val uploaded_by_id: String,
    val uploaded_by_name: String,
    val user_image: String,
    val post_image_urls: ArrayList<String>

): Serializable {
    constructor() : this("","","", arrayListOf(), "", "", "", "", arrayListOf())
}