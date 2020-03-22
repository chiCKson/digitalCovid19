package com.company_name.digital_covid19.methods


import android.content.SharedPreferences

class Methods {

    fun addSharedPreference(key:String,value:String,preferences: SharedPreferences){
        preferences
            .edit()  // create an Editor
            .putString(key, value)
            .apply()
    }

    fun readSharedPreferences(key:String,preferences: SharedPreferences):String?{
        return preferences.getString(key,"null")
    }
}