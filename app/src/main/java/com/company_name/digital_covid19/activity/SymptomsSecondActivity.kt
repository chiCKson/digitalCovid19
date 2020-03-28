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
import com.yeyint.customalertdialog.CustomAlertDialog
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
		breathingDifficultiesDialog()

	}
	private fun breathingDifficultiesDialog(){
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle("Answer these questins")
		dialogVerifyEmail.setAlertMessage("Have you feeling any breathing difficulties in past few weeks?")
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton("No") {
			symptom.breathin = false
			headacheswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton("Yes"){
			symptom.breathin = true
			headacheswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();
	}
	private fun onAddSymptomsPressed() {
		this.addSymptoms()
		methodObj.addSharedPreference("addSymptom","yes",sharedPreferences)
		TastyToast.makeText(applicationContext, "Symptom added Successfully.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
		this.startHomeActivity()
	}
	

	
	private fun headacheswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle("Answer these questins")
		dialogVerifyEmail.setAlertMessage("Have you feeling any headache in past few weeks?")
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton("No") {
			symptom.headache = false
			oncoughswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton("Yes"){
			symptom.headache = true
			oncoughswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();

	}
	
	private fun oncoughswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle("Answer these questins")
		dialogVerifyEmail.setAlertMessage("Have you feeling dry cough in past few weeks?")
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)

		dialogVerifyEmail.setNegativeButton("No") {
			symptom.cough = false
			onsorethroatswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton("Yes"){
			symptom.cough= true
			onsorethroatswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();

	}
	
	private fun onsorethroatswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle("Answer these questins")
		dialogVerifyEmail.setAlertMessage("Have you feeling sore throat in past few weeks?")
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton("No") {
			symptom.throat = false
			onfeverswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton("Yes"){
			symptom.throat= true
			onfeverswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();


	}
	
	private fun onfeverswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle("Answer these questins")
		dialogVerifyEmail.setAlertMessage("Have you feeling sore throat in past few weeks?")
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton("No") {
			symptom.fever = false
			this.onAddSymptomsPressed()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton("Yes"){
			symptom.fever= true
			this.onAddSymptomsPressed()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();


	}
	
	private fun startHomeActivity() {
	
		this.startActivity(MapsActivity.newIntent(this))
		finish()
	}

}
