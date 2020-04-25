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
import com.company_name.digital_covid19.DirectConnectionActivity
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


		// Configure Event component
		binding.eventButton.setOnClickListener {
			this.onEventPressed()
		}

		// Configure Transport component
		binding.transportButton.setOnClickListener {
			this.onTransportPressed()
		}
		binding.connectionButton.setOnClickListener {
			onConnectionPressed()
		}
	}
	
	private fun onClosePressed() {
	
		this.finish()
	}
	
	private fun onConnectionPressed() {
	
		this.startAddLocationActivity()
	}
	
	private fun onEventPressed() {
	
		this.addEvent()
	}
	private fun addEvent() {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(getString(R.string.event_type))
		dialogVerifyEmail.setAlertMessage(getString(R.string.event_type_details))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton(resources.getString(R.string.public_label)) {
			this.startAddEventModalActivity()

			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(resources.getString(R.string.private_label)){

			this.startAddLocationActivity()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();


	}
	private fun onTransportPressed() {
		val dialog = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialog.setAlertTitle(resources.getString(R.string.traveling_Method))
		dialog.setAlertMessage(resources.getString(R.string.traveling_q))
		dialog.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		dialog.setPositiveButton(resources.getString(R.string.public_label) ){
			val dialogBusTrain = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
			dialogBusTrain.setAlertTitle(resources.getString(R.string.traveling_Method))
			dialogBusTrain.setAlertMessage(resources.getString(R.string.public_traveling_method))
			dialogBusTrain.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
			dialogBusTrain.setPositiveButton(resources.getString(R.string.bus)) {
				Toast.makeText(applicationContext, "Bus Button Clicked", Toast.LENGTH_SHORT).show()
				dialogBusTrain.dismiss()
			}
			dialogBusTrain.setNegativeButton(resources.getString(R.string.train)) {
				Toast.makeText(applicationContext, "Train Button Clicked", Toast.LENGTH_SHORT).show()
				dialogBusTrain.cancel()
			}
			dialogBusTrain.create();
			dialogBusTrain.show();
			dialog.dismiss()
		}
		dialog.setNegativeButton(resources.getString(R.string.private_label)) {
			Toast.makeText(applicationContext, "Private Button Clicked", Toast.LENGTH_SHORT).show()
			dialog.cancel()
		}
		dialog.create();
		dialog.show();
	}
	
	private fun startAddLocationActivity() {
	
		this.startActivity(DirectConnectionActivity.newIntent(this))
		finish()
	}
	
	private fun startAddEventModalActivity() {
	
		this.startActivity(AddEventModalActivity.newIntent(this))
		finish()
	}
}
