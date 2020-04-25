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
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.SymptomsSecondActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.DigitalCovidCommonViewModel
import com.company_name.digital_covid19.models.Symptom
import com.company_name.digital_covid19.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast
import com.yeyint.customalertdialog.CustomAlertDialog
import io.github.pierry.progress.Progress
import java.util.*


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
	private val model: DigitalCovidCommonViewModel by viewModels()
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
		val statusObserver = Observer<Boolean> { set ->
			this.init()

		}
		model.userSet.observe(this, statusObserver)

		binding.addSymptomButton.setOnClickListener {
			symptom.history=binding.historyEditText.text.toString()
			this.onAddSymptomsPressed()
		}
		binding.skipButton.setOnClickListener {
			symptom.history=binding.historyEditText.text.toString()
			this.onAddSymptomsPressed()
		}
	}
	private fun getUserData(){
		methodObj.progressDialogShow(progressDialog,resources.getString(R.string.loading))
		val userListener = object : ValueEventListener {
			override fun onDataChange(dataSnapshot: DataSnapshot) {
				for(child in dataSnapshot.children){
					val user = child.getValue(User::class.java)


					if (mAuth.currentUser?.phoneNumber == user?.mobile){

						currentUserNic= user!!.nic.toString()

						methodObj.addSharedPreference("currentUserNic",user!!.nic.toString(),sharedPreferences)
						methodObj.progressDialogDismiss(progressDialog)
						model.userSet.value=true
						return
					}

				}
				TastyToast.makeText(applicationContext, getString(R.string.first_register),
						TastyToast.LENGTH_LONG, TastyToast.ERROR)

				startActivity(Intent(this@SymptomsSecondActivity,RegisterActivity::class.java))
			}
			override fun onCancelled(databaseError: DatabaseError) {
				val e=databaseError.toException()
				methodObj.progressDialogDismiss(progressDialog)
				TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
						TastyToast.LENGTH_LONG, TastyToast.ERROR)
			}
		}
		database.child("users").addValueEventListener(userListener)
	}
	private fun addSymptoms( ) {
		var age=0
		if (currentUserNic.length==10){
			val year: Int = Calendar.getInstance().get(Calendar.YEAR)
			val ex=currentUserNic.substring(0,2)
			val bYear="19$ex".toInt()
			age=year-bYear
		}else{
			val year: Int = Calendar.getInstance().get(Calendar.YEAR)
			val bYear=currentUserNic.substring(0,4).toInt()

			age=year-bYear
		}
		symptom.age=age
		database.child("symptoms").child(currentUserNic).setValue(symptom)
	}
	
	private fun init() {
		symptom.abroad= getValue()
		if (getValue())
			symptom.country = valueCountry
		// Configure addSymptoms component
		addSex()

	}
	private fun breathingDifficultiesDialog(){
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(resources.getString(R.string.symptoms_q))
		dialogVerifyEmail.setAlertMessage(resources.getString(R.string.breathing_difficulties))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton(resources.getString(R.string.symptom_activity_no_button_hint)) {
			symptom.breathin = false
			headacheswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(resources.getString(R.string.symptom_activity_yes_button_hint)){
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
		TastyToast.makeText(applicationContext, resources.getString(R.string.symptom_success), TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
		this.startHomeActivity()
	}
	

	
	private fun headacheswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(resources.getString(R.string.symptoms_q))
		dialogVerifyEmail.setAlertMessage(resources.getString(R.string.headache_label))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton(resources.getString(R.string.symptom_activity_no_button_hint)) {
			symptom.headache = false
			oncoughswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(resources.getString(R.string.symptom_activity_yes_button_hint)){
			symptom.headache = true
			oncoughswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();

	}
	
	private fun oncoughswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(resources.getString(R.string.symptoms_q))
		dialogVerifyEmail.setAlertMessage(resources.getString(R.string.cough_label))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)

		dialogVerifyEmail.setNegativeButton(resources.getString(R.string.symptom_activity_no_button_hint)) {
			symptom.cough = false
			onsorethroatswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(resources.getString(R.string.symptom_activity_yes_button_hint)){
			symptom.cough= true
			onsorethroatswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();

	}
	
	private fun onsorethroatswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(resources.getString(R.string.symptoms_q))
		dialogVerifyEmail.setAlertMessage(resources.getString(R.string.sorethroat_label))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton(resources.getString(R.string.symptom_activity_no_button_hint)) {
			symptom.throat = false
			onfeverswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(resources.getString(R.string.symptom_activity_yes_button_hint)){
			symptom.throat= true
			onfeverswitchValueChanged()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();


	}
	private fun addSex() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(resources.getString(R.string.symptoms_q))
		dialogVerifyEmail.setAlertMessage(resources.getString(R.string.gender_label))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton(resources.getString(R.string.male)) {
			symptom.sex = "male"
			breathingDifficultiesDialog()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(resources.getString(R.string.female)){
			symptom.sex= "female"
			breathingDifficultiesDialog()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();


	}
	
	private fun onfeverswitchValueChanged() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(resources.getString(R.string.symptoms_q))
		dialogVerifyEmail.setAlertMessage(resources.getString(R.string.fever_label))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton(resources.getString(R.string.symptom_activity_no_button_hint)) {
			symptom.fever = false

			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(resources.getString(R.string.symptom_activity_yes_button_hint)){
			symptom.fever= true

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
