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
		
		fun newIntent(context: Context,abroaded:Boolean): Intent {
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
	
		this.startHomeActivity()
	}
	
	private fun breathingdifficultiesswitchValueChanged() {
	
	}
	
	private fun headacheswitchValueChanged() {
	
	}
	
	private fun oncoughswitchValueChanged() {
	
	}
	
	private fun onsorethroatswitchValueChanged() {
	
	}
	
	private fun onfeverswitchValueChanged() {
	
	}
	
	private fun startHomeActivity() {
	
		this.startActivity(MapsActivity.newIntent(this))
	}
}
