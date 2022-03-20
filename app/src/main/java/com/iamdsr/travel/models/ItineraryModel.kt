package com.iamdsr.travel.models

data class ItineraryModel (
    val itinerary_id: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val day: Long,
    val type: String,
    val journey_mode: String,
    val from: String,
    val to: String,
    val hotel_name: String,
    val hotel_address: String,
    val timestamp: String,
    val trip_id: String,
    val trip_title: String,
    val user_id: String,
    val position: Int){
    constructor() : this("","","","","",0,"","",
        "","","","","","","","", 0)

    override fun toString(): String {
        return "ItineraryModel(itinerary_id='$itinerary_id', title='$title', description='$description'" +
                ", start_date='$date', start_time='$time', day=$day, type='$type', journey_mode='$journey_mode'" +
                ", from='$from', to='$to', hotel_name='$hotel_name', hotel_address='$hotel_address', timestamp='$timestamp', trip_id='$trip_id'" +
                ", trip_title='$trip_title', user_id='$user_id', position=$position)"
    }


}