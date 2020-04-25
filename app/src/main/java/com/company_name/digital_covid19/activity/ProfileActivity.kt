package com.company_name.digital_covid19.activity

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityProfileBinding
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.User
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast
import kotlinx.android.synthetic.main.activity_maps.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var methodObj: Methods
    private lateinit var dialog: ACProgressPie
    private lateinit var binding:ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        methodObj= Methods()
        dialog = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        dialog.show()
        database = FirebaseDatabase.getInstance().reference
        val iin = intent
        val b = iin.extras
        if (b != null) {
            val nic = b.getString("nic")
            //Toast.makeText(this@VerifyNumberActivity, mobile, Toast.LENGTH_LONG).show()
            getUserData(nic)

        }
        binding.buttonMenuMyprofile.setOnClickListener {
            finish()
        }
    }
    private fun getUserData(nic:String){

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                binding.etEmailMyprofile.setText(user!!.email)
                binding.etMobileMyprofile.setText(user.mobile)
                binding.etNameMyprofile.setText(user.username)
                binding.etNicMyprofile.setText(user.nic)

                dialog.dismiss()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("users").child(nic!!).addValueEventListener(userListener)
    }
}
