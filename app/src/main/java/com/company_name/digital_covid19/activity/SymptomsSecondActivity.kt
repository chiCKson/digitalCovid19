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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sdsmdg.tastytoast.TastyToast


class SymptomsSecondActivity: AppCompatActivity() {

	companion object {

		fun newIntent(context: Context): Intent {

			return Intent(context, SymptomsSecondActivity::class.java)
		}

	}
	
	private lateinit var binding: SymptomsSecondActivityBinding
	private lateinit var symptom: Symptom
	private lateinit var sharedPreferences: SharedPreferences
	private lateinit var currentUserNic:String
	private lateinit var database: DatabaseReference
	override fun onCreate(savedInstanceState: Bundle?) {

		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.symptoms_second_activity)
		database = FirebaseDatabase.getInstance().reference
		val obj= Methods()
		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)

		if (obj.readSharedPreferences("currentUserNic",sharedPreferences)!="null") {
			currentUserNic= obj.readSharedPreferences("currentUserNic",sharedPreferences).toString()
		}
		this.init()
	}
	private fun addSymptoms( ) {
		database.child("symptoms").child(currentUserNic).setValue(symptom)
	}
	
	private fun init() {
		symptom.abroad = false
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
	}
}
