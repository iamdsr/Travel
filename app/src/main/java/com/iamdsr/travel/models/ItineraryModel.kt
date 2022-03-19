package com.iamdsr.travel.models

import java.sql.Timestamp

data class ItineraryModel (
    val itinerary_id: String,
    val title: String,
    val description: String,
    val date: String,
    val day: Long,
    val type: String,
    val journey_mode: String,
    val from: String,
    val to: String,
    val hotel_name: String,
    val hotel_address: String,
    val time: String,
    val timestamp: String,
    val trip_id: String,
    val trip_title: String,
    val user_id: String){
    constructor() : this("","","","",0,"","","",
        "","","","","","","","")

    override fun toString(): String {
        return "ItineraryModel(itinerary_id='$itinerary_id', title='$title', description='$description', date='$date', day=$day, " +
                "type='$type', journey_mode='$journey_mode', from='$from', to='$to', hotel_name='$hotel_name', hotel_address='$hotel_address', " +
                "time='$time', timestamp='$timestamp', trip_id='$trip_id', trip_title='$trip_title', user_id='$user_id')"
    }


}