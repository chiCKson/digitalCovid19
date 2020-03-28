/*
*  RegisterActivity.kt
*  digitalCovid19
*
*  Created by Erandra Jayasundara.
*  Copyright © 2018 keliya. All rights reserved.
*/

package com.company_name.digital_covid19.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.RegisterActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.User
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sdsmdg.tastytoast.TastyToast
import io.github.pierry.progress.Progress


class RegisterActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, RegisterActivity::class.java)
		}
	}
	var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
	private lateinit var binding: RegisterActivityBinding
	private lateinit var mAuth: FirebaseAuth
	private lateinit var database: DatabaseReference
	private lateinit var sharedPreferences: SharedPreferences
    private lateinit var methodObj:Methods
    private lateinit var progressDialog: Progress
	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private lateinit var latLng: LatLng
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		mAuth = FirebaseAuth.getInstance()
		Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
		val placesClient = Places.createClient(this)
		database = FirebaseDatabase.getInstance().reference
        methodObj= Methods()
        progressDialog=methodObj.progressDialog(this)
		binding = DataBindingUtil.setContentView(this, R.layout.register_activity)
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
		this.init()
	}
	private fun validateForm():Boolean{
		if (binding.selectLocationButton.text=="Choose Location"){
			TastyToast.makeText(applicationContext, "Please select the location.",
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}

		if (binding.nicEditText.text.toString()=="" || binding.passwordEditText.text.toString()=="" || binding.emailEditText.text.toString()==""){
			TastyToast.makeText(applicationContext, "All fileds should be filled.",
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}
		if (binding.passwordEditText.text.length<8){
			TastyToast.makeText(applicationContext, "Password should be atleast 8 characters long.",
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			binding.passwordEditText.background=ContextCompat.getDrawable(this,R.drawable.error_edit_text)
			return false
		}
		val regexNicOld = """\d{9}[vV]""".toRegex()
		val regexNicNew = """\d{11}""".toRegex()
		if (!regexNicOld.matches(binding.nicEditText.text.toString()))
			if(!regexNicNew.matches(binding.nicEditText.text.toString())){
				TastyToast.makeText(applicationContext, "Error in National Identity Card Number.",
						TastyToast.LENGTH_LONG, TastyToast.ERROR)
				binding.nicEditText.background=ContextCompat.getDrawable(this,R.drawable.error_edit_text)
				return false
			}

		if(binding.passwordEditText.text.toString()!=binding.confirmPasswordEditText.text.toString()){
			TastyToast.makeText(applicationContext, "Entered passwords are not match.",
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			binding.confirmPasswordEditText.background=ContextCompat.getDrawable(this,R.drawable.error_edit_text)
			return false
		}
		val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
		if (!emailPattern.matches(binding.emailEditText.text.toString())){
			TastyToast.makeText(applicationContext, "You did not enter a valid email address.",
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			binding.emailEditText.background=ContextCompat.getDrawable(this,R.drawable.error_edit_text)
			return false
		}
		return true
	}
	private fun writeNewUser( nic:String,name: String, email: String,mobile:String) {
        methodObj.addSharedPreference("currentUserNic",nic,sharedPreferences)
		methodObj.addSharedPreferenceInt("userLocId",1,sharedPreferences)
        methodObj.addSharedPreference("currentUserName",name,sharedPreferences)
		methodObj.addSharedPreference("secondUse","yes",sharedPreferences)
		methodObj.deletePreference(sharedPreferences,"addSymptom")
		val user = User(name, email,nic,mobile, latLng.latitude, latLng.longitude)
		database.child("users").child(nic).setValue(user)
	}
	private fun init() {

		// Configure Register component
		binding.registerButton.setOnClickListener {
			if (latLng!=null)
				this.onRegisterPressed()
		}
		binding.selectLocationButton.setOnClickListener {
			autoComplete()
		}
	}
	private fun makeGreen(){
		binding.emailEditText.background=ContextCompat.getDrawable(this,R.drawable.register_activity_password_edit_text_background)
		binding.confirmPasswordEditText.background=ContextCompat.getDrawable(this,R.drawable.register_activity_password_edit_text_background)
		binding.nicEditText.background=ContextCompat.getDrawable(this,R.drawable.register_activity_password_edit_text_background)
		binding.passwordEditText.background=ContextCompat.getDrawable(this,R.drawable.register_activity_password_edit_text_background)
	}
	
	private fun onRegisterPressed() {
		this.makeGreen()
		if (this.validateForm()){
			methodObj.progressDialogShow(progressDialog,"Please Wait!")
			this.registerUserOnDb()
		}

	}
	private fun registerUserOnDb(){
		val email=binding.emailEditText.text.toString()
		val password=binding.passwordEditText.text.toString()
		val name=binding.nameEditText.text.toString()
		val mobile=binding.mobileEditText.text.toString()
		val nic=binding.nicEditText.text.toString()
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						val user = mAuth.currentUser
						this.writeNewUser(nic,name,email,mobile)
                        methodObj.progressDialogDismiss(progressDialog)
						this.sendVerificationEmail()

					} else {
                        methodObj.progressDialogDismiss(progressDialog)
						TastyToast.makeText(applicationContext, "Authentication failed.${task.exception}",
								TastyToast.LENGTH_LONG, TastyToast.ERROR)

					}


				}
	}
	private fun sendVerificationEmail(){
		val user = FirebaseAuth.getInstance().currentUser
		user!!.sendEmailVerification()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						signout()
						startWelcomeActivity()
						finish()
					} else {
						TastyToast.makeText(applicationContext, "Error sending verification email.Please try again later. ",
								TastyToast.LENGTH_LONG, TastyToast.ERROR)
					}
				}
	}

	private fun signout() {
		mAuth.signOut()
		methodObj.deletePreference(sharedPreferences,"currentUserNic")
	}


	private fun startWelcomeActivity() {
		this.startActivity(WelcomeActivity.newIntent(this))
	}

	private fun startSymptomActivity() {
	
		this.startActivity(SymptomActivity.newIntent(this))
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

				latLng= place.latLng!!
				binding.selectLocationButton.text=place.name

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
