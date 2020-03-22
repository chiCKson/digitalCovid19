/*
*  AddTransportModalActivity.kt
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
import com.company_name.digital_covid19.databinding.AddTransportModalActivityBinding


class AddTransportModalActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, AddTransportModalActivity::class.java)
		}
	}
	
	private lateinit var binding: AddTransportModalActivityBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.add_transport_modal_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure Date component
		binding.dateButton.setOnClickListener({ view ->
			this.onDatePressed()
		})
		
		// Configure addTransport component
		binding.addtransportButton.setOnClickListener({ view ->
			this.onAddTransportPressed()
		})
	}
	
	fun onDatePressed() {
	
	}
	
	fun onAddTransportPressed() {
	
	}
}
