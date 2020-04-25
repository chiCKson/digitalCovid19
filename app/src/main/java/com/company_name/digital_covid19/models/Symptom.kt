package com.company_name.digital_covid19.models

import android.service.autofill.FillEventHistory
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Symptom(
        var abroad: Boolean?=false,
        var breathin: Boolean?=true,
        var fever:Boolean?=true,
        var cough:Boolean?=true,
        var throat:Boolean?=true,
        var headache:Boolean?=true,
        var country:String?="",
        var date:String?="",
        var smell:String="",
        var history: String="",
        var sex:String="",
        var age:Int=0

)