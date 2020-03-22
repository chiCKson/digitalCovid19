package com.company_name.digital_covid19.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Symptom(
        var abroad: Boolean?,
        var breathin: Boolean?,
        var fever:Boolean?,
        var cough:Boolean?,
        var country:String?="",
        var date:String?=""

)