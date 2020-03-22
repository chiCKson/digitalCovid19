/*
*  AddLocationActivity.kt
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
import com.company_name.digital_covid19.databinding.AddLocationActivityBinding


class AddLocationActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, AddLocationActivity::class.java)
		}
	}
	
	private lateinit var binding: AddLocationActivityBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.add_location_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure Close component
		binding.closeButton.setOnClickListener({ view ->
			this.onClosePressed()
		})
		
		// Configure Login component
		binding.loginButton.setOnClickListener({ view ->
			this.onLoginPressed()
		})
		
		// Configure DatePickLocation component
		binding.datepicklocationButton.setOnClickListener({ view ->
			this.onDatePickLocationPressed()
		})
	}
	
	fun onClosePressed() {
	
		this.finish()
	}
	
	fun onLoginPressed() {
	
	}
	
	fun onDatePickLocationPressed() {
	
	}
}
