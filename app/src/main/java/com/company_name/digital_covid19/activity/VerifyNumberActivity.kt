package com.company_name.digital_covid19.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityVerifyNumberBinding
import com.company_name.digital_covid19.methods.Methods
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.sdsmdg.tastytoast.TastyToast
import java.util.*
import java.util.concurrent.TimeUnit


class VerifyNumberActivity : AppCompatActivity() {
    companion object {

        fun newIntent(context: Context): Intent {

            // Fill the created intent with the data you want to be passed to this Activity when it's opened.
            return Intent(context, VerifyNumberActivity::class.java)
        }
    }

    private var mVerificationId: String? = null
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var methodObj: Methods
    //The edittext to input the code

    private lateinit var binding:ActivityVerifyNumberBinding
    //firebase auth object
    private lateinit var dialog:ACProgressPie
    private var mAuth: FirebaseAuth? = null
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_number)
        binding.resendButton.isEnabled=false
        dialog = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                // [END_EXCLUDE]
                dialog.show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                // [END_EXCLUDE]

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    TastyToast.makeText(applicationContext, "Invalid credentials",
                            TastyToast.LENGTH_LONG, TastyToast.ERROR)
                //    fieldPhoneNumber.error = "Invalid phone number."
                    // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    TastyToast.makeText(applicationContext, "Quota Exceeds",
                            TastyToast.LENGTH_LONG, TastyToast.WARNING)
                  //  Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                         //   Snackbar.LENGTH_SHORT).show()
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                TastyToast.makeText(applicationContext, getString(R.string.code_sent_verify),
                        TastyToast.LENGTH_LONG, TastyToast.SUCCESS)
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // [START_EXCLUDE]
                // Update UI

                // [END_EXCLUDE]
            }
        }


        methodObj= Methods()
        val iin = intent
        val b = iin.extras
        mAuth = FirebaseAuth.getInstance();
        if (b != null) {
            val mobile = b.getString("mobile")
            //Toast.makeText(this@VerifyNumberActivity, mobile, Toast.LENGTH_LONG).show()
            sendVerificationCode(mobile)

        }
        binding.resendButton.setOnClickListener {
            val iin = intent
            val b = iin.extras

            if (b != null) {
                val mobile = b.getString("mobile")
                //Toast.makeText(this@VerifyNumberActivity, mobile, Toast.LENGTH_LONG).show()
                resendVerificationCode(mobile,resendToken)

            }
        }
        binding.verifyButton.setOnClickListener {
            if (binding.codeEditText.text.toString()!="") {
                verifyPhoneNumberWithCode(storedVerificationId,binding.codeEditText.text.toString())
                //verifyVerificationCode(binding.codeEditText.text.toString())
            }
        }

    }

    private fun timer(count:Int){
        var counter=count
        var T = Timer()
        T.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (counter==0){
                        binding.resendButton.isEnabled=true
                        T.cancel()
                    }
                    binding.timerTextView.text=counter.toString()
                    counter--
                }
            }
        }, 1000, 1000)
    }
    private fun sendVerificationCode(mobile: String) {
        binding.resendButton.isEnabled=false
        timer(60)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+94$mobile",
                60,
                TimeUnit.SECONDS,
                this,
                callbacks)
    }
    // [START resend_verification]
    private fun resendVerificationCode(
            phoneNumber: String,
            token: PhoneAuthProvider.ForceResendingToken?
    ) {
        binding.resendButton.isEnabled=false
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+94$phoneNumber", // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                callbacks, // OnVerificationStateChangedCallbacks
                token) // ForceResendingToken from callbacks
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        dialog.show()
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        //verification successful we will start the profile activity
                        dialog.dismiss()
                        if (methodObj.readSharedPreferences("addSymptom",sharedPreferences)=="null") {
                            this.startSymptomActivity()
                        }else{
                            this.startHomeActivity()
                        }
                    } else {
                        dialog.dismiss()
                        //verification unsuccessful.. display an error message
                        var message = getString(R.string.went_wrong)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = getString(R.string.invalid_code)
                        }
                        TastyToast.makeText(applicationContext, message,
                                TastyToast.LENGTH_LONG, TastyToast.ERROR)
                        startWelcomeActivity()
                    }
                })
    }
    private fun startHomeActivity() {

        this.startActivity(MapsActivity.newIntent(this))
        finish()
    }
    private fun startWelcomeActivity() {
        this.startActivity(WelcomeActivity.newIntent(this))
        finish()
    }
    private fun startSymptomActivity() {
        this.startActivity(SymptomActivity.newIntent(this))
        finish()
    }
}
