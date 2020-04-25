package com.company_name.digital_covid19.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.DigitalCovidCommonViewModel
import com.company_name.digital_covid19.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast

import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity() {
    companion object {

        fun newIntent(context: Context): Intent {

            // Fill the created intent with the data you want to be passed to this Activity when it's opened.
            return Intent(context, MapsActivity::class.java)
        }
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var methodObj:Methods
    private lateinit var progressDialog: Progress
    private lateinit var currentUserDetails:User
    private lateinit var userList:ArrayList<User>
    private val model: DigitalCovidCommonViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        currentUserDetails=User()
        userList=ArrayList()
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        methodObj= Methods()
        progressDialog=methodObj.progressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        methodObj.progressDialogShow(progressDialog,resources.getString(R.string.loading))
        profile_image_view.setOnClickListener {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            intent.putExtra("nic", currentUserDetails.nic)
            startActivity(intent)
        }

        database = FirebaseDatabase.getInstance().reference
       if(methodObj.readSharedPreferences("lang",sharedPreferences)=="ta"){
           image_1.background=getDrawable(R.drawable.tamil1)
           image_2.background=getDrawable(R.drawable.tamil2)
           image_3.background=getDrawable(R.drawable.tamil3)
       }else if(methodObj.readSharedPreferences("lang",sharedPreferences)=="si"){
           image_1.background=getDrawable(R.drawable.sinhala1)
           image_2.background=getDrawable(R.drawable.sinhala2)
           image_3.background=getDrawable(R.drawable.sinhala3)
       }
        if (mAuth.currentUser==null)
            this.signOut()
        else{
            this.getUserData()
        }

        init()
        val statusObserver = Observer<String> { status ->

            if (status == "infected") {
                profilesectionbackground_constraint_layout.background = ContextCompat.getDrawable(applicationContext, R.drawable.red_back)
                profile_image_view.background = ContextCompat.getDrawable(applicationContext, R.drawable.red_profile_back)
                safe_text_view.text = resources.getString(R.string.infect)
            } else if (status == "possible1" && status != "infected") {
                profilesectionbackground_constraint_layout.background = ContextCompat.getDrawable(applicationContext, R.drawable.orange_back)
                profile_image_view.background = ContextCompat.getDrawable(applicationContext, R.drawable.orange_profile_back)
                safe_text_view.text = resources.getString(R.string.possible)
            } else if (status == "possible2" && status != "infected" && status != "possible1") {
                profilesectionbackground_constraint_layout.background = ContextCompat.getDrawable(applicationContext, R.drawable.yellow_back)
                profile_image_view.background = ContextCompat.getDrawable(applicationContext, R.drawable.yellow_profile_back)
                safe_text_view.text = resources.getString(R.string.possible)

            }
        }
        model.userStatus.observe(this, statusObserver)

        round_btn_pressed_li_button.setOnClickListener {
            this.onRoundBtnPressedLiPressed()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }


   private fun init(){
       val draw =drawer{
            divider{}
           primaryItem(resources.getString(R.string.menu_home)) {
               icon=R.drawable.ic_home
           }
           primaryItem (resources.getString(R.string.menu_Profile)){
               icon=R.drawable.ic_account_circle_black_24dp
               onClick { _ ->

                   val intent = Intent(applicationContext, ProfileActivity::class.java)
                   intent.putExtra("nic", currentUserDetails.nic)
                   startActivity(intent)
                   false
               }
           }
         /*  primaryItem(resources.getString(R.string.menu_family)) {
               icon=R.drawable.ic_group_black_24dp
               onClick { _ ->
                   startActivity(Intent(applicationContext,FamilyActivity::class.java))
                   false
               }
           }*/
           primaryItem(resources.getString(R.string.menu_symptom)) {
               icon=R.drawable.ic_favorite_black_24dp
               onClick { _ ->
                   startActivity(Intent(applicationContext,MySymptomActivity::class.java))
                   false
               }
           }
           primaryItem(getString(R.string.medical_advices_menu)) {
               icon=R.drawable.ic_local_hospital_black_24dp
               onClick { _ ->
                   startActivity(Intent(applicationContext,DoctorListActivity::class.java))
                   false
               }
           }
           primaryItem(resources.getString(R.string.menu_event)) {
               icon=R.drawable.ic_event_black_24dp
               onClick { _ ->
                   startActivity(Intent(applicationContext,MyEventActivity::class.java))
                   false
               }
           }
           primaryItem(resources.getString(R.string.menu_stat)){
               icon=R.drawable.ic_insert_chart_black_24dp
               onClick { _ ->
                   startActivity(Intent(applicationContext,StatisticActivity::class.java))
                   false
               }

           }
           primaryItem(getString(R.string.language_menu)){
               icon=R.drawable.ic_language_black_24dp
               onClick { _ ->
                   languageChange()
                   false
               }

           }
           primaryItem(resources.getString(R.string.menu_signout)) {
               icon=R.drawable.ic_exit_to_app_black_24dp
               onClick { _ ->
                   signOut()
                   false
               }
           }
       }
       menu.setOnClickListener {
           draw.openDrawer()
       }
   }
    private fun languageChange(){
        methodObj.addSharedPreference("lang","null",sharedPreferences)
        startActivity(Intent(applicationContext,LanguageActivity::class.java))
        finish()
    }
    private fun getUserData(){

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    val user = child.getValue(User::class.java)
                    userList.add(user!!)
                    if (mAuth.currentUser?.phoneNumber == user?.mobile){
                        currentUserDetails=user
                        john_smith_text_view.text= user!!.username
                        model.userStatus.value=user.status
                        methodObj.addSharedPreference("currentUserNic",user.nic.toString(),sharedPreferences)

                    }

                }
                methodObj.progressDialogDismiss(progressDialog)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                methodObj.progressDialogDismiss(progressDialog)
                TastyToast.makeText(applicationContext, resources.getString(R.string.error_db),
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("users").addValueEventListener(userListener)
    }


    private fun onRoundBtnPressedLiPressed() {

        this.startActivityModelActivity()
    }

    private fun startActivityModelActivity() {

        this.startActivity(ActivityModelActivity.newIntent(this))
    }
    private fun startWelcomeActivity() {

        this.startActivity(WelcomeActivity.newIntent(this))
    }
    private fun signOut(){
        if (mAuth.currentUser!=null) {
            FirebaseAuth.getInstance().signOut()
            methodObj.deletePreference(sharedPreferences,"secondUse")
            methodObj.deletePreference(sharedPreferences,"currentUserNic")

            this.startWelcomeActivity()
            finish()
        }
    }



}
