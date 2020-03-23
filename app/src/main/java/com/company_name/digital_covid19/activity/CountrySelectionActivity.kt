package com.company_name.digital_covid19.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityCountrySelectionBinding


class CountrySelectionActivity : AppCompatActivity() {
    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, CountrySelectionActivity::class.java)
        }
    }
    private lateinit var binding: ActivityCountrySelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_selection)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_country_selection)
        val countries: Array<out String> = resources.getStringArray(R.array.countries)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, countries).also { adapter ->
            binding.countryEditText.setAdapter(adapter)
        }
        this.init()
    }
    private fun init(){
        binding.countryEditText.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            this.startSymptomsSecondActivity(true,selectedItem)
        }
    }
    private fun startSymptomsSecondActivity(abroaded:Boolean,country:String) {

        this.startActivity(SymptomsSecondActivity.newIntent(this,abroaded,country))
    }

}
