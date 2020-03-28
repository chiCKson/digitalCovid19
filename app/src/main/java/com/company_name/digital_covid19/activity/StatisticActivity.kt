package com.company_name.digital_covid19.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityStatisticBinding
import com.company_name.digital_covid19.databinding.SymptomActivityBinding
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_statistic.*
import org.json.JSONObject


class StatisticActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                            binding.updatedLabel.text="These data updated on $time"
                            binding.localNewCasesTextview.text=data.getString("local_new_cases")
                            binding.localTotalCasesTextview.text=data.getString("local_total_cases")
                            binding.localHospitalCasesTextview.text=data.getString("local_total_number_of_individuals_in_hospitals")
                            binding.localDeathTextview.text=data.getString("local_deaths")
                            binding.localRecoveredTextview.text=data.getString("local_recovered")


                        }
                    }
                }

        httpAsync.join()
    }
}
