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
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
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
import com.google.firebase.database.*
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
	private  var userList:ArrayList<String> = ArrayList()
	var ref = FirebaseDatabase.getInstance().getReference("users")
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		mAuth = FirebaseAuth.getInstance()
		getUsers()
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
		if (binding.nameEditText.text.toString() in userList){
			TastyToast.makeText(applicationContext, getString(R.string.username_exist_error),
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}
		if (binding.selectLocationButton.text=="Choose Location"){
			TastyToast.makeText(applicationContext, resources.getString(R.string.location_select),
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}

		if (binding.nicEditText.text.toString()==""  || binding.mobileEditText.text.toString()==""){
			TastyToast.makeText(applicationContext, resources.getString(R.string.fill_all),
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}

		val regexNicOld = """\d{9}[vV]""".toRegex()
		val regexNicNew = """\d{12}""".toRegex()
		if (!regexNicOld.matches(binding.nicEditText.text.toString()))
			if(!regexNicNew.matches(binding.nicEditText.text.toString())){
				TastyToast.makeText(applicationContext, getString(R.string.error_nic),
						TastyToast.LENGTH_LONG, TastyToast.ERROR)
				binding.nicEditText.background=ContextCompat.getDrawable(this,R.drawable.error_edit_text)
				return false
			}
		val regexMobile = """\d{10}""".toRegex()
		if(!regexMobile.matches(binding.mobileEditText.text.toString())){
			TastyToast.makeText(applicationContext, getString(R.string.number_long_digit_check),
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}

		val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
		if (binding.emailEditText.text.toString()!="") {
			if (!emailPattern.matches(binding.emailEditText.text.toString())) {
				TastyToast.makeText(applicationContext, getString(R.string.error_valid_email),
						TastyToast.LENGTH_LONG, TastyToast.ERROR)
				binding.emailEditText.background = ContextCompat.getDrawable(this, R.drawable.error_edit_text)
				return false
			}
		}
		return true
	}
	private fun writeNewUser( nic:String,name: String, email: String,mobile:String) {
        methodObj.addSharedPreference("currentUserNic",nic,sharedPreferences)
		methodObj.addSharedPreferenceInt("userLocId",1,sharedPreferences)
        methodObj.addSharedPreference("currentUserName",name,sharedPreferences)
		methodObj.addSharedPreference("secondUse","yes",sharedPreferences)
		methodObj.deletePreference(sharedPreferences,"addSymptom")
		val user = User(name, email,nic,mobile,"safe", latLng.latitude, latLng.longitude)
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
	private fun makeGreen() {
		binding.emailEditText.background = ContextCompat.getDrawable(this, R.drawable.register_activity_password_edit_text_background)

		binding.nicEditText.background = ContextCompat.getDrawable(this, R.drawable.register_activity_password_edit_text_background)
	}
	
	private fun onRegisterPressed() {
		this.makeGreen()
		if (this.validateForm()){
			methodObj.progressDialogShow(progressDialog,resources.getString(R.string.loading))
			this.registerUserOnDb()
		}

	}
	private fun registerUserOnDb(){
		val email=binding.emailEditText.text.toString()

		val name=binding.nameEditText.text.toString()
		val mobile=binding.mobileEditText.text.toString()
		val nic=binding.nicEditText.text.toString()
		var num=mobile.substring(1)
		this.writeNewUser(nic,name,email,"+94$num")

		startVerifyActivity(num)
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
	private fun getUsers(){
		val dialog = ACProgressPie.Builder(this)
				.ringColor(Color.WHITE)
				.pieColor(Color.WHITE)
				.updateType(ACProgressConstant.PIE_AUTO_UPDATE)
				.build()
		dialog.show()
		val eventListener = object : ValueEventListener {
			override fun onDataChange(dataSnapshot: DataSnapshot) {

				for (child in dataSnapshot.children) {
					//Object object = child.getKey();
					val user=child.getValue(User::class.java)
					userList.add(user!!.username.toString())

				}
				dialog.dismiss()


			}

			override fun onCancelled(databaseError: DatabaseError) {
				val e=databaseError.toException()
				dialog.dismiss()
				TastyToast.makeText(applicationContext, getString(R.string.error_db),
						TastyToast.LENGTH_LONG, TastyToast.ERROR)
			}
		}
		ref.addValueEventListener(eventListener)
	}
	private fun startVerifyActivity(mobile:String) {

		val intent = Intent(this, VerifyNumberActivity::class.java)
		intent.putExtra("mobile", mobile)
		startActivity(intent)
		finish()

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
				Toast.makeText(this,resources.getString(R.string.error_db), Toast.LENGTH_LONG).show()
				//Log.i(FragmentActivity.TAG, status.getStatusMessage())
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// The user canceled the operation.
			}
		}
	}
}
