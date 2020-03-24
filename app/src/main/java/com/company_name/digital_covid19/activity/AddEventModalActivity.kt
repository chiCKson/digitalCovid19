/*
*  AddEventModalActivity.kt
*  digitalCovid19
*
*  Created by Erandra Jayasundara.
*  Copyright © 2018 keliya. All rights reserved.
*/

package com.company_name.digital_covid19.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.AddEventModalActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.Event
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
import java.util.*
import kotlin.collections.ArrayList


class AddEventModalActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, AddEventModalActivity::class.java)
		}
	}
	var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
	
	private lateinit var binding: AddEventModalActivityBinding
	var ref = FirebaseDatabase.getInstance().getReference()
	private lateinit var methodObj: Methods
	private var nic=""
	private lateinit var sharedPreferences: SharedPreferences
	private lateinit var geoFire: GeoFire
	private lateinit var latLng: LatLng
	private lateinit var gRef: DatabaseReference
	private var mYear=0
	private var mDay=0
	private  var mMonth=0
	private lateinit var events:ArrayList<String>
	override fun onCreate(savedInstanceState: Bundle?) {
		events= arrayListOf("test","test1","test2")
		gRef=ref.child("geofire/events")
		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
		methodObj= Methods()
		Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.add_event_modal_activity)
		ArrayAdapter(this, android.R.layout.simple_list_item_1, events).also { adapter ->
			binding.eventnameEditText.setAdapter(adapter)
		}
		this.init()
	}
	
	private fun init() {
		binding.locationEditText.setOnClickListener {
			this.autoComplete()
		}
		// Configure Close component
		binding.closeButton.setOnClickListener {
			this.onClosePressed()
		}

		// Configure Login component
		binding.pickDateButton.setOnClickListener {
			this.onpickDateButtonPressed()
		}

		// Configure Login component
		binding.addEventButton.setOnClickListener {
			this.onaddEventButtonPressed()
		}
	}
	
	private fun onClosePressed() {

		this.finish()
	}
	
	private fun onpickDateButtonPressed() {
		val c: Calendar = Calendar.getInstance()
		mYear = c.get(Calendar.YEAR)
		mMonth = c.get(Calendar.MONTH)
		mDay = c.get(Calendar.DAY_OF_MONTH)
		val datePickerDialog = DatePickerDialog(this,
				DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
					binding.pickDateButton.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
					val date = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
					geoFire = GeoFire(gRef.child(date))

				}, mYear, mMonth, mDay)
		datePickerDialog.show()
	}
	
	private fun onaddEventButtonPressed() {
		nic= methodObj.readSharedPreferences("currentUserNic",sharedPreferences).toString()
		if(binding.eventnameEditText.text.toString() in events){

			this.addDateUserWise(nic, binding.pickDateButton.text.toString() )
			this.addEventUserWise(nic,binding.eventnameEditText.text.toString())
			TastyToast.makeText(applicationContext, "Event added Successfully.",
						TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
				finish()



		}else{
			val key=binding.eventnameEditText.text.toString()
			if (latLng!=null) {
				geoFire.setLocation(key, GeoLocation(latLng.latitude, latLng.longitude))
				this.addNewEvent(key,binding.pickDateButton.text.toString(),binding.locationEditText.text.toString())
				this.addDateUserWise(nic, binding.pickDateButton.text.toString() )
				this.addEventUserWise(nic,binding.eventnameEditText.text.toString())
				TastyToast.makeText(applicationContext, "Event added Successfully.",
						TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
				finish()
			}else
				TastyToast.makeText(applicationContext, "Please Select a location",
						TastyToast.LENGTH_LONG, TastyToast.ERROR)
		}

	}
	private fun addDateUserWise( nic:String,date: String) {
		ref.child("userWiseDates").child(nic).push().setValue(date)
	}
	private fun addEventUserWise(nic:String,event: String){
		ref.child("eventWiseUsers").child(event).push().setValue(nic)
	}
	private fun addNewEvent(name:String,date:String,place:String){
		val event=Event(name,date,place)
		ref.child("events").child(name).setValue(event)

	}
	private fun autoComplete(){
		val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
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
				Toast.makeText(this,"error$status", Toast.LENGTH_LONG).show()
				//Log.i(FragmentActivity.TAG, status.getStatusMessage())
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// The user canceled the operation.
			}
		}
	}
}
