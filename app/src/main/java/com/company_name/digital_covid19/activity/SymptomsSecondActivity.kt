/*
*  SymptomsSecondActivity.kt
*  digitalCovid19
*
*  Created by Erandra Jayasundara.
*  Copyright © 2018 keliya. All rights reserved.
*/

package com.company_name.digital_covid19.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.SymptomsSecondActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.Symptom
import com.company_name.digital_covid19.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast
import io.github.pierry.progress.Progress


class SymptomsSecondActivity: AppCompatActivity() {

	companion object {
		var valueAbroad:Boolean=false
		var valueCountry:String=""
		fun newIntent(context: Context,abroaded:Boolean,country:String): Intent {
			valueAbroad=abroaded
			valueCountry=country
			return Intent(context, SymptomsSecondActivity::class.java)
		}
		fun getValue():Boolean{
			return valueAbroad
		}

	}
	private lateinit var mAuth: FirebaseAuth

	private lateinit var binding: SymptomsSecondActivityBinding
	private lateinit var symptom: Symptom
	private lateinit var sharedPreferences: SharedPreferences
	private lateinit var currentUserNic:String
	private lateinit var database: DatabaseReference
	private lateinit var progressDialog: Progress
	private lateinit var methodObj:Methods
	override fun onCreate(savedInstanceState: Bundle?) {

		super.onCreate(savedInstanceState)
		symptom=Symptom()
		methodObj= Methods()
		binding = DataBindingUtil.setContentView(this, R.layout.symptoms_second_activity)
		mAuth = FirebaseAuth.getInstance()
		progressDialog=methodObj.progressDialog(this)
		database = FirebaseDatabase.getInstance().reference

		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
		getUserData()
		this.init()
	}
	private fun getUserData(){
		methodObj.progressDialogShow(progressDialog,"Please Wait")
		val userListener = object : ValueEventListener {
			override fun onDataChange(dataSnapshot: DataSnapshot) {
				for(child in dataSnapshot.children){
					val user = child.getValue(User::class.java)
					if (mAuth.currentUser?.email == user?.email){

						currentUserNic= user!!.nic.toString()

						methodObj.addSharedPreference("currentUserNic",user!!.nic.toString(),sharedPreferences)
						methodObj.progressDialogDismiss(progressDialog)

						return
					}

				}
			}
			override fun onCancelled(databaseError: DatabaseError) {
				val e=databaseError.toException()

				TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
						TastyToast.LENGTH_LONG, TastyToast.ERROR)
			}
		}
		database.child("users").addValueEventListener(userListener)
	}
	private fun addSymptoms( ) {
		database.child("symptoms").child(currentUserNic).setValue(symptom)
	}
	
	private fun init() {
		symptom.abroad= getValue()
		if (getValue())
			symptom.country = valueCountry
		// Configure addSymptoms component
		binding.addsymptomsButton.setOnClickListener {
			this.onAddSymptomsPressed()
		}

		// Configure breathingDifficultiesSwitch component
		binding.breathingdifficultiesswitchSwitch.setOnClickListener {
			this.breathingdifficultiesswitchValueChanged()
		}

		// Configure HeadacheSwitch component
		binding.headacheswitchSwitch.setOnClickListener {

			this.headacheswitchValueChanged()
		}

		// Configure CoughSwitch component
		binding.coughswitchSwitch.setOnClickListener {
			this.oncoughswitchValueChanged()
		}

		// Configure soreThroatSwitch component
		binding.sorethroatswitchSwitch.setOnClickListener {
			this.onsorethroatswitchValueChanged()
		}

		// Configure FeverSwitch component
		binding.feverswitchSwitch.setOnClickListener {
			this.onfeverswitchValueChanged()
		}
	}
	
	private fun onAddSymptomsPressed() {
		this.addSymptoms()
		methodObj.addSharedPreference("addSymptom","yes",sharedPreferences)
		TastyToast.makeText(applicationContext, "Symptom added Successfully.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
		this.startHomeActivity()
	}
	
	private fun breathingdifficultiesswitchValueChanged() {
		symptom.breathin = binding.breathingdifficultiesswitchSwitch.isChecked
	}
	
	private fun headacheswitchValueChanged() {
		symptom.headache=binding.headacheswitchSwitch.isChecked
	}
	
	private fun oncoughswitchValueChanged() {
		symptom.cough=binding.coughswitchSwitch.isChecked
	}
	
	private fun onsorethroatswitchValueChanged() {
		symptom.throat=binding.sorethroatswitchSwitch.isChecked
	}
	
	private fun onfeverswitchValueChanged() {
		symptom.fever=binding.feverswitchSwitch.isChecked
	}
	
	private fun startHomeActivity() {
	
		this.startActivity(MapsActivity.newIntent(this))
		finish()
	}

}
