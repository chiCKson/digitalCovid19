package com.company_name.digital_covid19.methods


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.company_name.digital_covid19.R
import io.github.pierry.progress.Progress




class Methods {

    fun addSharedPreference(key:String,value:String,preferences: SharedPreferences){
        preferences
            .edit()  // create an Editor
            .putString(key, value)
            .apply()
    }
    fun deletePreference(preferences: SharedPreferences,key: String){
        preferences.edit().remove(key).apply();
    }
    fun addSharedPreferenceBoolean(key:String,value:Boolean,preferences: SharedPreferences){
        preferences
                .edit()  // create an Editor
                .putBoolean(key, value)
                .apply()
    }
    fun readSharedPreferencesBoolean(key:String,preferences: SharedPreferences):Boolean?{
        return preferences.getBoolean(key,false)
    }
    fun addSharedPreferenceInt(key:String,value:Int,preferences: SharedPreferences){
        preferences
                .edit()  // create an Editor
                .putInt(key, value)
                .apply()
    }
    fun readSharedPreferencesInt(key:String,preferences: SharedPreferences):Int?{
        return preferences.getInt(key,-1)
    }
    fun readSharedPreferences(key:String,preferences: SharedPreferences):String?{
        return preferences.getString(key,"null")
    }
    fun progressDialog(context: Context):Progress{
        var progress=Progress(context)
        progress.setBackgroundColor(ContextCompat.getColor(context,R.color.welcome_activity_register_button_text_color))
                .setMessageColor(ContextCompat.getColor(context,R.color.register_activity_constraint_layout_constraint_layout_background_color))
                .setProgressColor(ContextCompat.getColor(context,R.color.register_activity_constraint_layout_constraint_layout_background_color))

         return progress
    }
    fun progressDialogShow(progress: Progress,message:String){
        progress.setMessage(message)
        progress.show()
    }
    fun progressDialogDismiss(progress: Progress){
        progress.dismiss()
    }
}