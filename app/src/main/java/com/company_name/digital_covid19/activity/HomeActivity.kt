/*
*  HomeActivity.kt
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
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.HomeActivityBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng


class HomeActivity: AppCompatActivity(),OnMapReadyCallback {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, HomeActivity::class.java)
		}
	}
	
	private lateinit var binding: HomeActivityBinding
	private lateinit var gMap:GoogleMap

	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.home_activity)

		this.init()
	}
	
	private fun init() {

		// Configure Round Btn Pressed Li component
		binding.roundBtnPressedLiButton.setOnClickListener {
			this.onRoundBtnPressedLiPressed()
		}
	}
	
	private fun onRoundBtnPressedLiPressed() {
	
		this.startActivityModelActivity()
	}
	
	private fun startActivityModelActivity() {
	
		this.startActivity(ActivityModelActivity.newIntent(this))
	}

	override fun onMapReady(googleMap: GoogleMap) {

	}
}
