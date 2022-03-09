package com.iamdsr.travel.Models;

public class TripModel {
    private String trip_id, trip_title, trip_desc, journey_date, return_date, place_from, place_to, user_id;
    private long duration_in_days, total_heads;

    public TripModel(String trip_id, String trip_title, String trip_desc, String journey_date,
                     String return_date, String place_from, String place_to, String user_id, long duration_in_days, long total_heads) {
        this.trip_id = trip_id;
        this.trip_title = trip_title;
        this.trip_desc = trip_desc;
        this.journey_date = journey_date;
        this.return_date = return_date;
        this.place_from = place_from;
        this.place_to = place_to;
        this.user_id = user_id;
        this.duration_in_days = duration_in_days;
        this.total_heads = total_heads;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getTrip_title() {
        return trip_title;
    }

    public void setTrip_title(String trip_title) {
        this.trip_title = trip_title;
    }

    public String getTrip_desc() {
        return trip_desc;
    }

    public void setTrip_desc(String trip_desc) {
        this.trip_desc = trip_desc;
    }

    public String getJourney_date() {
        return journey_date;
    }

    public void setJourney_date(String journey_date) {
        this.journey_date = journey_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public String getPlace_from() {
        return place_from;
    }

    public void setPlace_from(String place_from) {
        this.place_from = place_from;
    }

    public String getPlace_to() {
        return place_to;
    }

    public void setPlace_to(String place_to) {
        this.place_to = place_to;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getDuration_in_days() {
        return duration_in_days;
    }

    public void setDuration_in_days(long duration_in_days) {
        this.duration_in_days = duration_in_days;
    }

    public long getTotal_heads() {
        return total_heads;
    }

    public void setTotal_heads(long total_heads) {
        this.total_heads = total_heads;
    }

    @Override
    public String toString() {
        return "TripModel{" +
                "trip_id='" + trip_id + '\'' +
                ", trip_title='" + trip_title + '\'' +
                ", trip_desc='" + trip_desc + '\'' +
                ", journey_date='" + journey_date + '\'' +
                ", return_date='" + return_date + '\'' +
                ", place_from='" + place_from + '\'' +
                ", place_to='" + place_to + '\'' +
                ", user_id='" + user_id + '\'' +
                ", duration_in_days=" + duration_in_days +
                ", total_heads=" + total_heads +
                '}';
    }
}
