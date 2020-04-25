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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cc.cloudist.acplibrary.ACProgressConstant
import cc.cloudist.acplibrary.ACProgressPie
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.DigitalCovidCommonViewModel
import com.company_name.digital_covid19.models.Event

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast

import kotlinx.android.synthetic.main.activity_my_event.*


class MyEventActivity : AppCompatActivity() {
    private var listEvent = ArrayList<Event>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var methodObj: Methods
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dialog: ACProgressPie
    private val model: DigitalCovidCommonViewModel by viewModels()
    private var userEventNameList=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_event)
        dialog = ACProgressPie.Builder(this)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build()
        mAuth = FirebaseAuth.getInstance()
        setSupportActionBar(toolbar_symptom)
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        supportActionBar!!.title=""
        methodObj= Methods()
        database = FirebaseDatabase.getInstance().reference

       menu.setOnClickListener {
          finish()
       }
        getUserEventNameList()
        val eventNameListObserver=Observer<Boolean>{set->

            getEvents()
        }
        model.eventNameListSet.observe(this,eventNameListObserver)
        //

        val eventListObserver = Observer<Boolean> { set ->
            var notesAdapter = EventsAdapter(this, listEvent)
            my_event_list_view.adapter = notesAdapter
            my_event_list_view.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
                //Toast.makeText(this, "Click on " + listEvent[position].name, Toast.LENGTH_SHORT).show()
            }
            if(listEvent.isEmpty()){
                TastyToast.makeText(applicationContext, getString(R.string.no_events_yet),
                        TastyToast.LENGTH_LONG, TastyToast.DEFAULT)
            }
        }
        model.eventListSet.observe(this,eventListObserver)

    }
    private fun getEvents(){
        val eventListner = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot!=null) {
                    for (child in dataSnapshot.children) {
                        if (child.key.toString() in (userEventNameList)) {
                            val event = child.getValue(Event::class.java)
                            listEvent.add(event!!)
                        }
                    }
                }else{
                    TastyToast.makeText(applicationContext, getString(R.string.no_events_yet),
                            TastyToast.LENGTH_LONG, TastyToast.DEFAULT)
                }
                model.eventListSet.value=true
                dialog.dismiss()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                dialog.dismiss()
                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("events").addValueEventListener(eventListner)
    }
    private fun getUserEventNameList(){
        dialog.show()
        val nic=methodObj.readSharedPreferences("currentUserNic",sharedPreferences)
        val eventListner = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    userEventNameList.add(child.value.toString())
                }
                model.eventNameListSet.value=true

            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                dialog.dismiss()
                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("userWiseEvents").child(nic!!).addValueEventListener(eventListner)
    }
    inner class EventsAdapter : BaseAdapter {

        private var eventList = ArrayList<Event>()
        private var context: Context? = null

        constructor(context: Context, eventList: ArrayList<Event>) : super() {
            this.eventList = eventList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.my_event_layout, parent, false)
                vh = ViewHolder(view)
                view.tag = vh

            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvEventName.text = eventList[position].name
            vh.tvLocation.text = eventList[position].place
            vh.tvDate.text=eventList[position].date

            return view
        }

        override fun getItem(position: Int): Any {
            return eventList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return eventList.size
        }
    }

    private class ViewHolder(view: View?) {
        val tvEventName: TextView
        val tvLocation: TextView
        val tvDate: TextView

        init {
            this.tvEventName = view?.findViewById(R.id.tvEventName) as TextView
            this.tvLocation = view?.findViewById(R.id.tvLocation) as TextView
            this.tvDate = view?.findViewById(R.id.tvDate) as TextView
        }

        //  if you target API 26, you should change to:
//        init {
//            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
//            this.tvContent = view?.findViewById<TextView>(R.id.tvContent) as TextView
//        }
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
