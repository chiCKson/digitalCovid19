package com.company_name.digital_covid19.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.databinding.ActivityDoctorListBinding
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.DigitalCovidCommonViewModel
import com.company_name.digital_covid19.models.Event
import com.company_name.digital_covid19.models.User
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_my_event.*

class DoctorListActivity : AppCompatActivity() {
    var ref = FirebaseDatabase.getInstance().getReference("geofire/doctor")
    var uRef = FirebaseDatabase.getInstance().getReference("users")
    var dRef = FirebaseDatabase.getInstance().getReference("admin")
    lateinit var doctorList:ArrayList<Doctor>
    var geoFire = GeoFire(ref)
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var methodObj: Methods
    private lateinit var binding:ActivityDoctorListBinding
    lateinit var  dialogs: ACProgressPie
    var found=false
    private lateinit var currentUserDetails:User
    private val model: DigitalCovidCommonViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_doctor_list)
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        setSupportActionBar(binding.toolbarDocterlist)
        supportActionBar!!.title=""
        methodObj= Methods()
        doctorList= ArrayList()
        dialogs = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        dialogs.show()
        currentUserDetails= User()
        getUserData()
        val userObserver = Observer<Boolean> { status ->
            getDoctors()
        }
        model.userSet.observe(this,userObserver)
        val control=Doctor("Control Unit","931421611v","+94123456789","doctor","Head Office",0.0,0.0)
        doctorList.add(control)
        var docterAdapter = DoctorListAdapter(this, doctorList)
        binding.doctorListView.adapter = docterAdapter
        val doctorListObserver= Observer<Boolean> { set->

            var docterAdapter = DoctorListAdapter(this, doctorList)
            binding.doctorListView.adapter = docterAdapter

            if(doctorList.isEmpty()){
                TastyToast.makeText(applicationContext, getString(R.string.no_events_yet),
                        TastyToast.LENGTH_LONG, TastyToast.DEFAULT)
            }
        }
        model.doctorListSet.observe(this,doctorListObserver)
        binding.backDoctorList.setOnClickListener {
            finish()
        }
        binding.doctorListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            //Toast.makeText(this, "Click on " + listEvent[position].name, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChatDoctorActivity::class.java)
            intent.putExtra("docId", doctorList[position].mobile)
            startActivity(intent)
        }
    }
    private fun getUserData(){
        val nic =methodObj.readSharedPreferences("currentUserNic",sharedPreferences)
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user!=null) {
                    currentUserDetails = user
                    model.userSet.value=true
                }
                dialogs.dismiss()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                dialogs.dismiss()
                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        uRef.child(nic!!).addValueEventListener(userListener)
    }
    private fun getDoctors(){
        val dialog = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        dialog.show()
        val geoQuery: GeoQuery = geoFire!!.queryAtLocation(GeoLocation(currentUserDetails.lat, currentUserDetails.long), 7.0)
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {

                dRef.addValueEventListener( object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        doctorList.clear()
                        for (dsp in dataSnapshot.getChildren()) {
                            if(key==dsp.key.toString()){
                                val doctor=dsp.getValue(Doctor::class.java)
                                doctorList.add(doctor!!)
                                //found=true
                            }

                        }
                        dialog.dismiss()

                        model.doctorListSet.value=true
                    }
                    override fun onCancelled(error: DatabaseError) {
                        dialog.dismiss()
                        TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                                TastyToast.LENGTH_LONG, TastyToast.ERROR)
                    }
                })
            }

            override fun onKeyExited(key: String) {
               // Log.i("TAG", String.format("Provider %s is no longer in the search area", key))
                // dialog.hide()
                dialog.dismiss()
            }

            override fun onKeyMoved(key: String, location: GeoLocation) {
                //Log.i("TAG", String.format("Provider %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude))
                // dialog.hide()
                dialog.dismiss()
            }

            override fun onGeoQueryReady() {
               // Log.i("TAG", "onGeoQueryReady")
                dialog.dismiss()
                ////dialog.hide()
            }

            override fun onGeoQueryError(error: DatabaseError) {
                //Log.e("TAG", "error: " + error)
                dialog.dismiss()
                // dialog.hide()
            }
        })
    }
    inner class DoctorListAdapter : BaseAdapter {

        private var docList = ArrayList<Doctor>()
        private var context: Context? = null

        constructor(context: Context, docList: ArrayList<Doctor>) : super() {
            this.docList = docList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.doctor_layout, parent, false)
                vh = ViewHolder(view)
                view.tag = vh

            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvDocName.text = docList[position].username
            vh.tvHospital.text = docList[position].place


            return view
        }

        override fun getItem(position: Int): Any {
            return docList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return docList.size
        }
    }

    private class ViewHolder(view: View?) {
        val tvDocName: TextView
        val tvHospital: TextView


        init {
            this.tvDocName = view?.findViewById(R.id.tvDocName) as TextView
            this.tvHospital = view?.findViewById(R.id.tvHospital) as TextView

        }

        //  if you target API 26, you should change to:
//        init {
//            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
//            this.tvContent = view?.findViewById<TextView>(R.id.tvContent) as TextView
//        }
    }
}
@IgnoreExtraProperties
data class Doctor(
        var username: String? = "",
        var nic:String?="",
        var mobile:String?="",
        var type:String?="",
        var place:String?="",
        var lat:Double=6.927079,
        var long:Double=79.861244
)
