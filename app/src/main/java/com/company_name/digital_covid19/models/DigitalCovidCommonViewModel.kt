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
    val userLocation:MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }

    // Rest of the ViewModel...
}