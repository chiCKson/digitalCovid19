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
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.RegisterActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sdsmdg.tastytoast.TastyToast
import io.github.pierry.progress.Progress


class RegisterActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
		
			// Fill the created intent with the data you want to be passed to this Activity when it's opened.
			return Intent(context, RegisterActivity::class.java)
		}
	}
	
	private lateinit var binding: RegisterActivityBinding
	private lateinit var mAuth: FirebaseAuth
	private lateinit var database: DatabaseReference
	private lateinit var sharedPreferences: SharedPreferences
    private lateinit var methodObj:Methods
    private lateinit var progressDialog: Progress
	override fun onCreate(savedInstanceState: Bundle?) {
	
		super.onCreate(savedInstanceState)
		mAuth = FirebaseAuth.getInstance()
		database = FirebaseDatabase.getInstance().reference
        methodObj= Methods()
        progressDialog=methodObj.progressDialog(this)
		binding = DataBindingUtil.setContentView(this, R.layout.register_activity)


		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
		this.init()
	}
	private fun validateForm():Boolean{
		if (binding.emailEditText.text.toString()=="")
			return false
		if (binding.passwordEditText.text.toString()=="")
			return false
		if (binding.nicEditText.text.toString()=="")
			return false
		if (binding.passwordEditText.text.length<8)
			return false
		return true
	}
	private fun writeNewUser( nic:String,name: String, email: String,mobile:String) {

        methodObj.addSharedPreference("currentUserNic",nic,sharedPreferences)
        methodObj.addSharedPreference("currentUserName",name,sharedPreferences)
		val user = User(name, email,nic,mobile)
		database.child("users").child(nic).setValue(user)
	}
	private fun init() {

		// Configure Register component
		binding.registerButton.setOnClickListener {
            methodObj.progressDialogShow(progressDialog,"Please Wait! Registering to our servers.")
			this.onRegisterPressed()
		}
	}
	
	private fun onRegisterPressed() {
		if (this.validateForm()){
			this.registerUserOnDb()
		}else{
			TastyToast.makeText(applicationContext, "Please fill the form correctly. All field should fill, password should have atleast 8 characters.", TastyToast.LENGTH_LONG, TastyToast.ERROR)
			//Toast.makeText(this@RegisterActivity, "Please fill the form correctly. All field should fill, password should have atleast 8 characters.",Toast.LENGTH_SHORT).show()
		}

	}
	private fun registerUserOnDb(){
		val email=binding.emailEditText.text.toString()
		val password=binding.passwordEditText.text.toString()
		val name=binding.nameEditText.text.toString()
		val mobile=binding.mobileEditText.text.toString()
		val nic=binding.nicEditText.text.toString()
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						val user = mAuth.currentUser
						this.writeNewUser(nic,name,email,mobile)
                        methodObj.progressDialogDismiss(progressDialog)
						this.startSymptomActivity()
						finish()
					} else {
                        methodObj.progressDialogDismiss(progressDialog)
						TastyToast.makeText(applicationContext, "Authentication failed.${task.exception}",
								TastyToast.LENGTH_LONG, TastyToast.ERROR)

					}


				}
	}
	
	private fun startSymptomActivity() {
	
		this.startActivity(SymptomActivity.newIntent(this))
	}
}
