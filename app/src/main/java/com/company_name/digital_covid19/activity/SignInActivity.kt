/*
*  SignInActivity.kt
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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.SignInActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.google.firebase.auth.FirebaseAuth
import com.sdsmdg.tastytoast.TastyToast
import com.yeyint.customalertdialog.CustomAlertDialog
import io.github.pierry.progress.Progress


class SignInActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, SignInActivity::class.java)
		}
	}
	
	private lateinit var binding: SignInActivityBinding
	private lateinit var mAuth: FirebaseAuth
	private lateinit var methodObj:Methods
	private lateinit var progressDialog: Progress
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		mAuth = FirebaseAuth.getInstance()
		methodObj= Methods()
		progressDialog=methodObj.progressDialog(this)
		binding = DataBindingUtil.setContentView(this, R.layout.sign_in_activity)
		this.init()
	}
	
	private fun init() {
	
		// Configure Register component
		binding.registerButton.setOnClickListener {
			if(binding.registerButton.text.toString()!="Cancel")
				this.onRegisterPressed()
			else
				this.changeUiForLogin()
		}

		// Configure Login component
		binding.loginButton.setOnClickListener {

			if (binding.loginButton.text.toString()!="Reset")
				this.onLoginPressed()
			else {
				if (binding.emailEditText.text.toString()!="")
					this.sendForgetPasswordEmail(binding.emailEditText.text.toString())
				else
					TastyToast.makeText(applicationContext, "Please enter your email that previously registered.", TastyToast.LENGTH_LONG, TastyToast.ERROR)
			}
		}

		// Configure g+ component
		binding.gButton.setOnClickListener {
			this.onGPressed()
		}

		// Configure facebook component
		binding.facebookButton.setOnClickListener{
			this.onFacebookPressed()
		}
		binding.forgotPasswordTextView.setOnClickListener {
			this.changeUiForPasswordChange()
		}
	}

	private fun changeUiForLogin() {
		binding.loginButton.text="Login"
		binding.registerButton.text="Register"
		binding.passwordEditText.visibility= View.VISIBLE
	}

	private fun changeUiForPasswordChange() {
		binding.loginButton.text="Reset"
		binding.registerButton.text="Cancel"
		binding.passwordEditText.visibility= View.INVISIBLE
	}


	private fun sendForgetPasswordEmail(email:String) {
		methodObj.progressDialogShow(progressDialog,"Please Wait!")
		FirebaseAuth.getInstance().sendPasswordResetEmail(email)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						TastyToast.makeText(applicationContext, "Your password reset mail has been sent your email provided.Log in to your email and follow the instructions.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
						startWelcomActivity()
						finish()
					}else{
						methodObj.progressDialogDismiss(progressDialog)
						TastyToast.makeText(applicationContext, "Error sending the email.", TastyToast.LENGTH_LONG, TastyToast.ERROR)
					}
				}
	}

	private fun onRegisterPressed() {
	
		this.startRegisterActivity()
	}
	private fun startWelcomActivity(){
		this.startActivity(WelcomeActivity.newIntent(this))
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
		methodObj.progressDialogShow(progressDialog,"Please Wait!")
		val email=binding.emailEditText.text.toString()
		val password=binding.passwordEditText.text.toString()
		mAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						// Sign in success, update UI with the signed-in user's information
						//Log.d(FragmentActivity.TAG, "signInWithEmail:success")
						val user = mAuth.currentUser
						methodObj.progressDialogDismiss(progressDialog)
						this.startHomeActivity()
						finish()
					} else {
						// If sign in fails, display a message to the user.
						methodObj.progressDialogDismiss(progressDialog)
						TastyToast.makeText(applicationContext, "Authentication failed.Please check your credentials.", TastyToast.LENGTH_LONG, TastyToast.ERROR)

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
		finish()
	}
	
	private fun startHomeActivity() {
	
		this.startActivity(MapsActivity.newIntent(this))
		finish()
	}
}
