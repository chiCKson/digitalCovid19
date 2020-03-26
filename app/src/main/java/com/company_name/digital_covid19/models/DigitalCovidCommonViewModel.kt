package com.company_name.digital_covid19.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DigitalCovidCommonViewModel : ViewModel() {

    // Create a LiveData with a String
    val mMapSet: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val userStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // Rest of the ViewModel...
}