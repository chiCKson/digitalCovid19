/*
*  ActivityModelActivity.kt
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
import com.company_name.digital_covid19.databinding.ActivityModelActivityBinding
import com.company_name.digital_covid19.dialog.ActivityModelActivityTransportButtonSheet


class ActivityModelActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, ActivityModelActivity::class.java)
		}
	}
	
	private lateinit var binding: ActivityModelActivityBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_model_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure Close component
		binding.closeButton.setOnClickListener {
			this.onClosePressed()
		}

		// Configure Location component
		binding.locationButton.setOnClickListener {
			this.onLocationPressed()
		}

		// Configure Event component
		binding.eventButton.setOnClickListener {
			this.onEventPressed()
		}

		// Configure Transport component
		binding.transportButton.setOnClickListener {
			this.onTransportPressed()
		}
	}
	
	private fun onClosePressed() {
	
		this.finish()
	}
	
	private fun onLocationPressed() {
	
		this.startAddLocationActivity()
	}
	
	private fun onEventPressed() {
	
		this.startAddEventModalActivity()
	}
	
	private fun onTransportPressed() {
	
		ActivityModelActivityTransportButtonSheet().show(this.supportFragmentManager, "ActivityModelActivityTransportButtonSheet")
	}
	
	private fun startAddLocationActivity() {
	
		this.startActivity(AddLocationActivity.newIntent(this))
	}
	
	private fun startAddEventModalActivity() {
	
		this.startActivity(AddEventModalActivity.newIntent(this))
	}
}
