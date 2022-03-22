package com.iamdsr.travel.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.iamdsr.travel.models.TripModel


open class MySharedPreferences(context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(AppConstants.TRIP_ID_SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE)

    open fun setTripModel(model: TripModel) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(model)
        editor.putString("TRIP_MODEL",json)
        editor.apply()
    }

    open fun getTripModel(): TripModel {
        val gson = Gson()
        val json: String? = sharedPreferences.getString("TRIP_MODEL", "")
        return gson.fromJson(json, TripModel::class.java)
    }
}
