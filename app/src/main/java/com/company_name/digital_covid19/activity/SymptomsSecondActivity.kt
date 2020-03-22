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
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.SymptomsSecondActivityBinding


class SymptomsSecondActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, SymptomsSecondActivity::class.java)
		}
	}
	
	private lateinit var binding: SymptomsSecondActivityBinding
	
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.symptoms_second_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure addSymptoms component
		binding.addsymptomsButton.setOnClickListener({ view ->
			this.onAddSymptomsPressed()
		})
		
		// Configure breathingDifficultiesSwitch component
		binding.breathingdifficultiesswitchSwitch.setOnClickListener({ view ->
			this.onGroup334ValueChanged()
		})
		
		// Configure HeadacheSwitch component
		binding.headacheswitchSwitch.setOnClickListener({ view ->
			this.onGroup335ValueChanged()
		})
		
		// Configure CoughSwitch component
		binding.coughswitchSwitch.setOnClickListener({ view ->
			this.onGroup336ValueChanged()
		})
		
		// Configure soreThroatSwitch component
		binding.sorethroatswitchSwitch.setOnClickListener({ view ->
			this.onRectangle1475ValueChanged()
		})
		
		// Configure FeverSwitch component
		binding.feverswitchSwitch.setOnClickListener({ view ->
			this.onRectangle566ValueChanged()
		})
	}
	
	fun onAddSymptomsPressed() {
	
		this.startHomeActivity()
	}
	
	fun onGroup334ValueChanged() {
	
	}
	
	fun onGroup335ValueChanged() {
	
	}
	
	fun onGroup336ValueChanged() {
	
	}
	
	fun onRectangle1475ValueChanged() {
	
	}
	
	fun onRectangle566ValueChanged() {
	
	}
	
	private fun startHomeActivity() {
	
		this.startActivity(HomeActivity.newIntent(this))
	}
}
