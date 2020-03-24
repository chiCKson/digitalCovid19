package com.company_name.digital_covid19.models


import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
        var name:String?="",
        var date:String?="",
        var place:String?=""
)