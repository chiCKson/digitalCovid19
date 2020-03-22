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
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.SignInActivityBinding


class SignInActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, SignInActivity::class.java)
		}
	}
	
	private lateinit var binding: SignInActivityBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.sign_in_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure Register component
		binding.registerButton.setOnClickListener({ view ->
			this.onRegisterPressed()
		})
		
		// Configure Login component
		binding.loginButton.setOnClickListener({ view ->
			this.onLoginPressed()
		})
		
		// Configure g+ component
		binding.gButton.setOnClickListener({ view ->
			this.onGPressed()
		})
		
		// Configure facebook component
		binding.facebookButton.setOnClickListener({ view ->
			this.onFacebookPressed()
		})
	}
	
	fun onRegisterPressed() {
	
		this.startRegisterActivity()
	}
	
	fun onLoginPressed() {
	
		this.startHomeActivity()
	}
	
	fun onGPressed() {
	
	}
	
	fun onFacebookPressed() {
	
	}
	
	private fun startRegisterActivity() {
	
		this.startActivity(RegisterActivity.newIntent(this))
	}
	
	private fun startHomeActivity() {
	
		this.startActivity(MapsActivity.newIntent(this))
	}
}
