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
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityModelActivityBinding
import com.yeyint.customalertdialog.CustomAlertDialog


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
		val dialog = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialog.setAlertTitle("Transport Method")
		dialog.setAlertMessage("Please choose you used transportation method in last few weeks.")
		dialog.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		dialog.setPositiveButton("Public") {
			val dialogBusTrain = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
			dialogBusTrain.setAlertTitle("Transport Method")
			dialogBusTrain.setAlertMessage("Please choose you used transportation method in last few weeks.")
			dialogBusTrain.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
			dialogBusTrain.setPositiveButton("Bus") {
				Toast.makeText(applicationContext, "Bus Button Clicked", Toast.LENGTH_SHORT).show()
				dialogBusTrain.dismiss()
			}
			dialogBusTrain.setNegativeButton("Train") {
				Toast.makeText(applicationContext, "Train Button Clicked", Toast.LENGTH_SHORT).show()
				dialogBusTrain.cancel()
			}
			dialogBusTrain.create();
			dialogBusTrain.show();
			dialog.dismiss()
		}
		dialog.setNegativeButton("Private") {
			Toast.makeText(applicationContext, "Private Button Clicked", Toast.LENGTH_SHORT).show()
			dialog.cancel()
		}
		dialog.create();
		dialog.show();
	}
	
	private fun startAddLocationActivity() {
	
		this.startActivity(AddLocationActivity.newIntent(this))
		finish()
	}
	
	private fun startAddEventModalActivity() {
	
		this.startActivity(AddEventModalActivity.newIntent(this))
		finish()
	}
}
