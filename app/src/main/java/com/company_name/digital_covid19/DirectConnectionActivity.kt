package com.company_name.digital_covid19

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import com.company_name.digital_covid19.databinding.ActivityDirectConnectionBinding
import com.company_name.digital_covid19.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sdsmdg.tastytoast.TastyToast

class DirectConnectionActivity : AppCompatActivity() {
    companion object {

        fun newIntent(context: Context): Intent {

            // Fill the created intent with the data you want to be passed to this Activity when it's opened.
            return Intent(context, DirectConnectionActivity::class.java)
        }
    }
    private lateinit var sharedPreferences: SharedPreferences
    private  var userNameList:ArrayList<String> = ArrayList()
    private  var userList:HashMap<String,User> = HashMap()
    private lateinit var currentUser: String
    private lateinit var binding:ActivityDirectConnectionBinding
    var ref = FirebaseDatabase.getInstance().getReference("users")
    var acRef = FirebaseDatabase.getInstance().getReference("userWiseConnection")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_direct_connection)
        setSupportActionBar(binding.toolbarDc)
        supportActionBar!!.title=""
        getUserList()
        binding.backButtonDc.setOnClickListener {
            finish()
        }
        binding.autoCompleteUsername.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedUser = parent.getItemAtPosition(position).toString()
            makeVisible()
            val user=userList[selectedUser]
            currentUser=user!!.nic.toString()
            var nic=""
            if (user!!.nic!!.length==12)
                nic="********${user.nic!!.substring(8,12)}"
            else
                nic="*****${user.nic!!.substring(5,10)}"
            binding.etNameDc.setText(user!!.username)
            binding.etNicDc.setText(nic)

        }
        binding.addConnectionButton.setOnClickListener {
            addConnection(currentUser)
        }
    }
    private fun addConnection(cnic:String){
        val nic=sharedPreferences.getString("currentUserNic","null")
        acRef.child(nic).child(cnic).setValue(cnic)
        TastyToast.makeText(applicationContext, getString(R.string.connection_Added),
                TastyToast.LENGTH_LONG, TastyToast.SUCCESS)

    }
    private fun makeVisible(){
        binding.addConnectionButton.visibility= View.VISIBLE
        binding.etNameDc.visibility= View.VISIBLE
        binding.etNicDc.visibility= View.VISIBLE
        binding.profileImageDc.visibility= View.VISIBLE
        binding.tvNameMyprofile.visibility= View.VISIBLE
        binding.tvNicMyprofile.visibility= View.VISIBLE
    }
    private fun getUserList(){
        val dialog = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        dialog.show()

        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                userNameList.clear()
                for (child in dataSnapshot.children) {
                    //Object object = child.getKey();
                    val user=child.getValue(User::class.java)
                    userList.put(user!!.username.toString(),user)
                    userNameList.add(user!!.username.toString())

                }
                dialog.dismiss()
                ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, userNameList).also { adapter ->
                    binding.autoCompleteUsername.setAdapter(adapter)
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                dialog.dismiss()
                TastyToast.makeText(applicationContext, getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        ref.addValueEventListener(eventListener)
    }
}
