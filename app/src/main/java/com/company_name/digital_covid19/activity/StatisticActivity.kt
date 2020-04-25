package com.company_name.digital_covid19.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityStatisticBinding
import com.company_name.digital_covid19.databinding.SymptomActivityBinding
import com.company_name.digital_covid19.methods.Methods
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_statistic.*
import org.json.JSONObject


class StatisticActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var methodObj: Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        methodObj= Methods()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistic)





        val httpAsync = "https://hpb.health.gov.lk/api/get-current-statistical"
                .httpGet()
                .responseString { request, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            println(ex)
                        }
                        is Result.Success -> {
                            val res = result.get()

                            // Convert String to json object

                            // Convert String to json object
                            val json = JSONObject(res)
                            val data = json.getJSONObject("data")
                            val time=data.getString("update_date_time")
                            var timeLabel=resources.getString(R.string.updatedIn)
                            binding.updatedLabel.text="$timeLabel - $time."
                            binding.localNewCasesTextview.text=data.getString("local_new_cases")
                            binding.localTotalCasesTextview.text=data.getString("local_total_cases")
                            binding.localHospitalCasesTextview.text=data.getString("local_total_number_of_individuals_in_hospitals")
                            binding.localDeathTextview.text=data.getString("local_deaths")
                            binding.localRecoveredTextview.text=data.getString("local_recovered")


                        }
                    }
                }

        httpAsync.join()
        binding.menu.setOnClickListener {
            finish()
        }
    }
    private fun startWelcomeActivity() {

        this.startActivity(WelcomeActivity.newIntent(this))
    }
    private fun languageChange(){
        methodObj.addSharedPreference("lang","null",sharedPreferences)
        startActivity(Intent(applicationContext,LanguageActivity::class.java))
        finish()
    }
    private fun signOut(){
        if (mAuth.currentUser!=null) {
            mAuth.signOut()
            methodObj.deletePreference(sharedPreferences,"secondUse")
            methodObj.deletePreference(sharedPreferences,"currentUserNic")

            this.startWelcomeActivity()
            finish()
        }
    }
}
