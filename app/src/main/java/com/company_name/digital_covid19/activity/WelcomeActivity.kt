/*
*  WelcomeActivity.kt
*  digitalCovid19
*
*  Created by Erandra Jayasundara.
*  Copyright © 2018 keliya. All rights reserved.
*/

package com.company_name.digital_covid19.activity

import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.WelcomeActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yeyint.customalertdialog.CustomAlertDialog


class WelcomeActivity: AppCompatActivity() {

	companion object {
		
		fun newIntent(context: Context): Intent {
			return Intent(context, WelcomeActivity::class.java)
		}
	}
	
	private lateinit var binding: WelcomeActivityBinding
	private lateinit var mAuth: FirebaseAuth
	private lateinit var sharedPreferences: SharedPreferences
	private lateinit var methodObj: Methods
	private lateinit var dialog: ACProgressPie
	private lateinit var database: DatabaseReference
	override fun onCreate(savedInstanceState: Bundle?) {
		methodObj= Methods()
		sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
		super.onCreate(savedInstanceState)
		//Toast.makeText(this,methodObj.readSharedPreferences("lang",sharedPreferences),Toast.LENGTH_LONG).show()
		mAuth = FirebaseAuth.getInstance()
		binding = DataBindingUtil.setContentView(this, R.layout.welcome_activity)
		dialog = ACProgressPie.Builder(this)
				.ringColor(Color.WHITE)
				.pieColor(Color.WHITE)
				.updateType(ACProgressConstant.PIE_AUTO_UPDATE)
				.build()
		database = FirebaseDatabase.getInstance().getReference("appversion/public")

		if (checkAppVersion()){
			startAnimationOne()
			init()
		}

	}
	private fun checkAppVersion():Boolean{
		dialog.show()
		var same=true
		val manager = packageManager
		var info: PackageInfo? = null
		try {
			info = manager.getPackageInfo(packageName, 0)
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}
		assert(info != null)
		var version = info!!.versionName
		val userListener = object : ValueEventListener {
			override fun onDataChange(dataSnapshot: DataSnapshot) {
				val app = dataSnapshot.getValue(AppVersion::class.java)
				dialog.dismiss()
				if (version!=app!!.v.toString())
					download(app.link.toString())

			}
			override fun onCancelled(databaseError: DatabaseError) {
				val e=databaseError.toException()

				dialog.dismiss()

				startAnimationOne()
				init()
			}
		}
		database.addValueEventListener(userListener)
		return same
	}
	private fun download(link: String) {
		val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.CURVE)
		dialogVerifyEmail.setAlertTitle(getString(R.string.update_title))
		dialogVerifyEmail.setAlertMessage(getString(R.string.update_app_label))
		dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.SUCCESS)
		//dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
		dialogVerifyEmail.setNegativeButton(getString(R.string.cancel)) {
			startAnimationOne()
			init()
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.setPositiveButton(getString(R.string.update_download)){
			val browserIntent =
					Intent(Intent.ACTION_VIEW, Uri.parse(link))
			startActivity(browserIntent)
			dialogVerifyEmail.dismiss()
		}
		dialogVerifyEmail.create();
		dialogVerifyEmail.show();


	}
	private fun init() {
		if (mAuth.currentUser!=null){
			if (methodObj.readSharedPreferences("addSymptom",sharedPreferences)=="null") {
				this.startSymptomActivity()
			} else
				this.startHomeActivity()

		}
		// Configure Register component
		binding.registerButton.setOnClickListener {
			this.onRegisterPressed()
		}

		// Configure Login component
		binding.loginButton.setOnClickListener {
			this.onLoginPressed()
		}


	}
	
	private fun onRegisterPressed() {
		this.startRegisterActivity()
	}
	
	private fun onLoginPressed() {
	
		this.startSignInActivity()
	}
	
	private fun onGPressed() {
	
	}
	
	private fun onFacebookPressed() {
	
	}
	
	private fun startSignInActivity() {
	
		this.startActivity(SignInActivity.newIntent(this))
		finish()
	}
	private fun startRegisterActivity() {

		this.startActivity(RegisterActivity.newIntent(this))
		finish()
	}
	private fun startHomeActivity() {

		this.startActivity(MapsActivity.newIntent(this))
		finish()
	}
	private fun startSymptomActivity() {
		this.startActivity(SymptomActivity.newIntent(this))
		finish()
	}
	private fun startAnimationOne() {
	
		val animator1 = ObjectAnimator.ofPropertyValuesHolder(binding.registerButton, PropertyValuesHolder.ofKeyframe(View.SCALE_X, Keyframe.ofFloat(0f, 0.3f), Keyframe.ofFloat(0.2f, 1.1f), Keyframe.ofFloat(0.4f, 0.9f), Keyframe.ofFloat(0.6f, 1.03f), Keyframe.ofFloat(0.8f, 0.97f), Keyframe.ofFloat(1f, 1f)), PropertyValuesHolder.ofKeyframe(View.SCALE_Y, Keyframe.ofFloat(0f, 0.3f), Keyframe.ofFloat(0.2f, 1.1f), Keyframe.ofFloat(0.4f, 0.9f), Keyframe.ofFloat(0.6f, 1.03f), Keyframe.ofFloat(0.8f, 0.97f), Keyframe.ofFloat(1f, 1f)))
		animator1.duration = 1000
		animator1.interpolator = PathInterpolatorCompat.create(0.22f, 0.61f, 0.36f, 1f)
		
		val animator2 = ObjectAnimator.ofPropertyValuesHolder(binding.registerButton, PropertyValuesHolder.ofKeyframe(View.ALPHA, Keyframe.ofFloat(0f, 0f), Keyframe.ofFloat(0.6f, 1f), Keyframe.ofFloat(1f, 1f)))
		animator2.duration = 1000
		animator2.interpolator = PathInterpolatorCompat.create(0.22f, 0.61f, 0.36f, 1f)
		
		val animatorSet1 = AnimatorSet()
		animatorSet1.playTogether(animator1, animator2)
		animatorSet1.setTarget(binding.registerButton)
		
		val animator3 = ObjectAnimator.ofPropertyValuesHolder(binding.loginButton, PropertyValuesHolder.ofKeyframe(View.SCALE_X, Keyframe.ofFloat(0f, 0.3f), Keyframe.ofFloat(0.2f, 1.1f), Keyframe.ofFloat(0.4f, 0.9f), Keyframe.ofFloat(0.6f, 1.03f), Keyframe.ofFloat(0.8f, 0.97f), Keyframe.ofFloat(1f, 1f)), PropertyValuesHolder.ofKeyframe(View.SCALE_Y, Keyframe.ofFloat(0f, 0.3f), Keyframe.ofFloat(0.2f, 1.1f), Keyframe.ofFloat(0.4f, 0.9f), Keyframe.ofFloat(0.6f, 1.03f), Keyframe.ofFloat(0.8f, 0.97f), Keyframe.ofFloat(1f, 1f)))
		animator3.duration = 1000
		animator3.interpolator = PathInterpolatorCompat.create(0.22f, 0.61f, 0.36f, 1f)
		
		val animator4 = ObjectAnimator.ofPropertyValuesHolder(binding.loginButton, PropertyValuesHolder.ofKeyframe(View.ALPHA, Keyframe.ofFloat(0f, 0f), Keyframe.ofFloat(0.6f, 1f), Keyframe.ofFloat(1f, 1f)))
		animator4.duration = 1000
		animator4.interpolator = PathInterpolatorCompat.create(0.22f, 0.61f, 0.36f, 1f)
		
		val animatorSet2 = AnimatorSet()
		animatorSet2.playTogether(animator3, animator4)
		animatorSet2.setTarget(binding.loginButton)
		

		

	}
}

@IgnoreExtraProperties
data class AppVersion(
		var v: String? = "",
		var link:String?=""
)
