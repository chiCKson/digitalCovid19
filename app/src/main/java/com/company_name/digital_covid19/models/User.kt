package com.company_name.digital_covid19.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var username: String? = "",
        var email: String? = "",
        var nic:String?="",
        var mobile:String?="",
        var lat:Double=6.927079,
        var long:Double=79.861244
)