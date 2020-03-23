package com.company_name.digital_covid19.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var username: String? = "",
        var email: String? = "",
        var nic:String?="",
        var mobile:String?=""
)