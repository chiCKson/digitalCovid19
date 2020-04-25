package com.company_name.digital_covid19.activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityLanguageBinding
import com.company_name.digital_covid19.methods.Methods
import java.util.*


class LanguageActivity : AppCompatActivity() {
    private lateinit var methodObj: Methods
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding:ActivityLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language)
        methodObj= Methods()
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        if (methodObj.readSharedPreferences("lang",sharedPreferences)=="ta"){
            setLocale("ta")
        }else if (methodObj.readSharedPreferences("lang",sharedPreferences)=="si"){
            setLocale("si")
        }else if (methodObj.readSharedPreferences("lang",sharedPreferences)=="en"){
            startActivity(Intent(this,WelcomeActivity::class.java))
            finish()
        }
        binding.tamil.setOnClickListener {
            methodObj.addSharedPreference("lang","ta",sharedPreferences)
            setLocale("ta")
        }
        binding.english.setOnClickListener {
            methodObj.addSharedPreference("lang","en",sharedPreferences)
            setLocale("en")
            finish()
        }
        binding.sinhala.setOnClickListener {
            methodObj.addSharedPreference("lang","si",sharedPreferences)
            setLocale("si")
        }
    }
    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        val refresh = Intent(this, LanguageActivity::class.java)
        finish()
        startActivity(Intent(this,WelcomeActivity::class.java))
    }
}
