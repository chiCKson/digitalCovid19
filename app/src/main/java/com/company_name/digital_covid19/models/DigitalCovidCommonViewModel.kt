package com.company_name.digital_covid19.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class DigitalCovidCommonViewModel : ViewModel() {

    // Create a LiveData with a String
    val mMapSet: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val userStatus: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val userSet:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val eventNameListSet:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val eventListSet:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val doctorListSet:MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    // Rest of the ViewModel...
}