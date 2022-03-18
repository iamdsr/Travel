package com.iamdsr.travel.models

data class TripModel (
    val trip_id: String,
    val trip_title: String,
    val trip_desc: String,
    val journey_date: String,
    val return_date: String,
    val place_from: String,
    val place_to: String,
    val user_id: String,
    val date_created: String,
    val journey_mode: String,
    val duration_in_days : Long,
    val total_heads : Long
    ) {
    constructor() : this("","","","","","","","","","",0,0)

    override fun toString(): String {
        return "TripModel(trip_id='$trip_id', trip_title='$trip_title', trip_desc='$trip_desc', journey_date='$journey_date', return_date='$return_date', place_from='$place_from', place_to='$place_to', user_id='$user_id', date_created='$date_created', journey_mode='$journey_mode', duration_in_days=$duration_in_days, total_heads=$total_heads)"
    }

}