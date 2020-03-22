/*
*  SignInActivity.kt
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.SignInActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.sdsmdg.tastytoast.TastyToast


class SignInActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, SignInActivity::class.java)
		}
	}
	
	private lateinit var binding: SignInActivityBinding
	private lateinit var mAuth: FirebaseAuth
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		mAuth = FirebaseAuth.getInstance()
		binding = DataBindingUtil.setContentView(this, R.layout.sign_in_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure Register component
		binding.registerButton.setOnClickListener {
			this.onRegisterPressed()
		}

		// Configure Login component
		binding.loginButton.setOnClickListener {
			this.onLoginPressed()
		}

		// Configure g+ component
		binding.gButton.setOnClickListener {
			this.onGPressed()
		}

		// Configure facebook component
		binding.facebookButton.setOnClickListener{
			this.onFacebookPressed()
		}
	}
	
	private fun onRegisterPressed() {
	
		this.startRegisterActivity()
	}
	
	private fun onLoginPressed() {

		if (this.validateForm())
			this.signin()

	}
	
	private fun onGPressed() {
	
	}
	
	private fun onFacebookPressed() {
	
	}
	private fun signin(){
		val email=binding.emailEditText.text.toString()
		val password=binding.passwordEditText.text.toString()
		mAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						// Sign in success, update UI with the signed-in user's information
						//Log.d(FragmentActivity.TAG, "signInWithEmail:success")
						val user = mAuth.currentUser
						this.startHomeActivity()
					} else {
						// If sign in fails, display a message to the user.

						TastyToast.makeText(applicationContext, "Authentication failed.${task.exception}", TastyToast.LENGTH_LONG, TastyToast.ERROR)

					}


				}
	}
	private fun validateForm():Boolean{
		if (binding.emailEditText.text.toString()=="")
			return false
		if (binding.passwordEditText.text.toString()=="")
			return false
		if (binding.passwordEditText.text.length<8)
			return false
		return true

	}
	
	private fun startRegisterActivity() {
	
		this.startActivity(RegisterActivity.newIntent(this))
	}
	
	private fun startHomeActivity() {
	
		this.startActivity(MapsActivity.newIntent(this))
	}
}
