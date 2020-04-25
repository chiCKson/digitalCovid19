package com.company_name.digital_covid19.models


import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
        var sender: String? = "",
        var receiver: String? = "",
        var message:String?=""
)