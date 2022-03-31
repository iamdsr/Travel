package com.iamdsr.travel.utils

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class Travel: Application() {

    private lateinit var sharedPreferenceHelper : MySharedPreferences
    override fun onCreate() {
        super.onCreate()
        sharedPreferenceHelper = MySharedPreferences(applicationContext);
        setUpAppTheme()
    }
    private fun setUpAppTheme() {
        if (sharedPreferenceHelper.getAppTheme() == AppConstants.DARK_THEME) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else if (sharedPreferenceHelper.getAppTheme() == AppConstants.LIGHT_THEME) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}