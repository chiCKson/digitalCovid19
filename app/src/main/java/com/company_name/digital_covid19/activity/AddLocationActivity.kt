/*
*  AddLocationActivity.kt
*  digitalCovid19
*
*  Created by Erandra Jayasundara.
*  Copyright © 2018 keliya. All rights reserved.
*/

package com.company_name.digital_covid19.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.AddLocationActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sdsmdg.tastytoast.TastyToast
import com.yeyint.customalertdialog.CustomAlertDialog
import java.util.*


class AddLocationActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, AddLocationActivity::class.java)
		}
	}
	var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
	private lateinit var binding: AddLocationActivityBinding
	private var mYear=0
	private var mDay=0
	private  var mMonth=0
	var ref = FirebaseDatabase.getInstance().getReference()
	private lateinit var methodObj:Methods
	private var locCount=0
	private var nic=""
	private lateinit var sharedPreferences: SharedPreferences
	private lateinit var geoFire: GeoFire
	private lateinit var latLng: LatLng
	private lateinit var gRef:DatabaseReference
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
		val placesClient = Places.createClient(this)
		gRef=ref.child("geofire/users")
		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
		methodObj= Methods()
		locCount= methodObj.readSharedPreferencesInt("userLocId",sharedPreferences)!!
		if (locCount==-1)
			locCount=0
		binding = DataBindingUtil.setContentView(this, R.layout.add_location_activity)
		this.init()
	}
	
	private fun init() {

		binding.locationEditText.setOnClickListener {
			this.onLocationEditTextPressed()
		}
		// Configure Close component
		binding.closeButton.setOnClickListener {
			this.onClosePressed()
		}

		// Configure Login component
		binding.addLocationButton.setOnClickListener {
			if (this.validateForm())
				this.onAddLocationPressed()
			else{
				val dialogError = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.DEFAULT)
				dialogError.setAlertTitle("Error")
				dialogError.setAlertMessage("Please be kind enough to fill all the fields.")
				dialogError.setDialogType(CustomAlertDialog.DialogType.ERROR)
				dialogError.setNegativeButton("Ok") {

					dialogError.cancel()
				}
				dialogError.create();
				dialogError.show();
			}
		}

		// Configure DatePickLocation component
		binding.datepicklocationButton.setOnClickListener {
			this.onDatePickLocationPressed()
		}
	}
	private fun validateForm():Boolean{
		if (binding.datepicklocationButton.text=="Pick a date")
			return false
		if (binding.locationEditText.text=="Choose Location")
			return false
		return true
	}
	private fun onClosePressed() {
	
		this.finish()
	}
	
	private fun onAddLocationPressed() {
		nic= methodObj.readSharedPreferences("currentUserNic",sharedPreferences).toString()
		val key="$nic-$locCount"
		if (latLng!=null) {
			locCount++
			methodObj.addSharedPreferenceInt("userLocId",locCount,sharedPreferences)
			geoFire.setLocation(key, GeoLocation(latLng.latitude, latLng.longitude))
			this.addDateUserWise(nic, binding.datepicklocationButton.text.toString() )
			TastyToast.makeText(applicationContext, "Location added Successfully.",
					TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
			finish()
			this.startHomeActivity()

		}else
			TastyToast.makeText(applicationContext, "Please Select a location",
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
	}
	private fun onLocationEditTextPressed(){
		this.autoComplete()
	}
	
	private fun onDatePickLocationPressed() {
		val c: Calendar = Calendar.getInstance()
		mYear = c.get(Calendar.YEAR)
		mMonth = c.get(Calendar.MONTH)
		mDay = c.get(Calendar.DAY_OF_MONTH)


		val datePickerDialog = DatePickerDialog(this,
				OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
					binding.datepicklocationButton.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
					val date=dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

					geoFire = GeoFire(gRef.child(date))
				}, mYear, mMonth, mDay)
		datePickerDialog.show()
	}
	private fun autoComplete(){
		val fields = listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG)
		val intent = Autocomplete.IntentBuilder(
						AutocompleteActivityMode.OVERLAY, fields).setCountry("LK")
				.build(this)
		startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
	}
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)

		if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				val place = Autocomplete.getPlaceFromIntent(data!!)
				binding.locationEditText.setText(place.name)
				latLng= place.latLng!!

				//Log.i(FragmentActivity.TAG, "Place: " + place.name + ", " + place.id)
			} else if (resultCode == AutocompleteActivity.RESULT_ERROR) {


				val status: Status = Autocomplete.getStatusFromIntent(data!!)
				Toast.makeText(this,"error$status",Toast.LENGTH_LONG).show()
				//Log.i(FragmentActivity.TAG, status.getStatusMessage())
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// The user canceled the operation.
			}
		}
	}
	private fun addDateUserWise( nic:String,date: String) {
		ref.child("userWiseDates").child(nic).push().setValue(date)
	}
	private fun startHomeActivity() {

		this.startActivity(MapsActivity.newIntent(this))
	}
}
