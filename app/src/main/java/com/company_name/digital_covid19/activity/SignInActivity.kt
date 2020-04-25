/*
*  SignInActivity.kt
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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.SignInActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.google.firebase.auth.FirebaseAuth
import com.sdsmdg.tastytoast.TastyToast


class SignInActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, SignInActivity::class.java)
		}
	}
	private lateinit var sharedPreferences: SharedPreferences
	private lateinit var binding: SignInActivityBinding
	private lateinit var mAuth: FirebaseAuth
	private lateinit var methodObj:Methods

	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
		mAuth = FirebaseAuth.getInstance()
		methodObj= Methods()

		binding = DataBindingUtil.setContentView(this, R.layout.sign_in_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure Register component
		binding.registerButton.setOnClickListener {
			this.onRegisterPressed()
		}

		// Configure Login component
		binding.loginButton.setOnClickListener {

				this.onLoginPressed()

		}



	}

	private fun onRegisterPressed() {
	
		this.startRegisterActivity()
	}
	private fun startWelcomActivity(){
		this.startActivity(WelcomeActivity.newIntent(this))
	}
	
	private fun onLoginPressed() {

		if (this.validateForm())
			this.signin(binding.emailEditText.text.toString())

	}
	

	private fun signin(mobile:String){
		var num=mobile.substring(1)
		val intent = Intent(this, VerifyNumberActivity::class.java)
		intent.putExtra("mobile", num)
		startActivity(intent)
		finish()
	}
	private fun validateForm():Boolean{
		if (binding.emailEditText.text.toString()==""){
			TastyToast.makeText(applicationContext, getString(R.string.number_not_empty),
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}
		val regexMobile = """\d{10}""".toRegex()
		if(!regexMobile.matches(binding.emailEditText.text.toString())){
			TastyToast.makeText(applicationContext, getString(R.string.number_long_digit_check),
					TastyToast.LENGTH_LONG, TastyToast.ERROR)
			return false
		}
		return true

	}
	
	private fun startRegisterActivity() {
	
		this.startActivity(RegisterActivity.newIntent(this))
		finish()
	}
	
	private fun startHomeActivity() {
	
		this.startActivity(MapsActivity.newIntent(this))
		finish()
	}
	private fun startWelcomeActivity() {
		this.startActivity(WelcomeActivity.newIntent(this))
		finish()
	}
	private fun startSymptomActivity() {
		this.startActivity(SymptomActivity.newIntent(this))
		finish()
	}
}
