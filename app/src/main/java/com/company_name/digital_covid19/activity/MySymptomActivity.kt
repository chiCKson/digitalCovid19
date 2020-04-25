package com.company_name.digital_covid19.activity

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityMySymptomBinding
import com.company_name.digital_covid19.models.Symptom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsmdg.tastytoast.TastyToast

class MySymptomActivity : AppCompatActivity() {
    private lateinit var  binding:ActivityMySymptomBinding
    private lateinit var sharedPreferences: SharedPreferences
    var ref = FirebaseDatabase.getInstance().getReference("symptoms")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_my_symptom)
        setSupportActionBar(binding.toolbarSymptom)
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        supportActionBar!!.title=""
        val nic=sharedPreferences.getString("currentUserNic","null")
        readSymptom(nic)
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.editButton.setOnClickListener {

            startActivity(Intent(this,SymptomActivity::class.java))
            finish()
        }
    }
    private fun readSymptom(nic:String){
        val dialog = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        dialog.show()
        val chatListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userSymptom=dataSnapshot.getValue(Symptom::class.java)
                binding.ageText.text=userSymptom!!.age.toString()
                binding.genderText.text=userSymptom.sex

                dialog.dismiss()
                var message=""
                if (userSymptom.abroad!!)
                    message += "I am been abroad in past few weeks. I visited to ${userSymptom.country} in the time period of ${userSymptom.date}."
                message += "I am feeling "
                if (userSymptom.fever!!)
                    message += "fever,"
                if (userSymptom.cough!!)
                    message += " dry cough,"
                if (userSymptom.headache!!)
                    message += " headache,"
                if (userSymptom.breathin!!)
                    message += " breathing difficulties,"
                if (userSymptom.throat!!)
                    message += " soar throat,"

                message+=" in past few weeks."

                binding.symptomText.text=message
                binding.historyText.text=userSymptom.history


            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                dialog.dismiss()
                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        ref.child(nic).addValueEventListener(chatListener)
    }
}
