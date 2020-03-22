/*
*  RegisterActivity.kt
*  digitalCovid19
*
*  Created by Erandra Jayasundara.
*  Copyright © 2018 keliya. All rights reserved.
*/

package com.company_name.digital_covid19.activity

import android.R.attr.password
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.RegisterActivityBinding
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, RegisterActivity::class.java)
		}
	}
	
	private lateinit var binding: RegisterActivityBinding
	private lateinit var mAuth: FirebaseAuth
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		mAuth = FirebaseAuth.getInstance()
		binding = DataBindingUtil.setContentView(this, R.layout.register_activity)
		this.init()
	}
	
	private fun init() {

		// Configure Register component
		binding.registerButton.setOnClickListener {
			this.onRegisterPressed()
		}
	}
	
	private fun onRegisterPressed() {
		val email=binding.emailEditText.text.toString()
		val password=binding.passwordEditText.text.toString()
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						val user = mAuth.currentUser
						this.startSymptomActivity()
					} else {
						Toast.makeText(this@RegisterActivity, "Authentication failed.${task.exception}",Toast.LENGTH_SHORT).show()
					}


				}

	}
	
	private fun startSymptomActivity() {
	
		this.startActivity(SymptomActivity.newIntent(this))
	}
}
