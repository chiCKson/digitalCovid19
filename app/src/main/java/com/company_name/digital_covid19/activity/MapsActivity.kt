package com.company_name.digital_covid19.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.DigitalCovidCommonViewModel
import com.company_name.digital_covid19.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast
import com.yeyint.customalertdialog.CustomAlertDialog
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {

        fun newIntent(context: Context): Intent {

            // Fill the created intent with the data you want to be passed to this Activity when it's opened.
            return Intent(context, MapsActivity::class.java)
        }
    }
    private lateinit var mMap: GoogleMap
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference
    private lateinit var methodObj:Methods
    private lateinit var progressDialog: Progress
    private lateinit var fusedLocationClient: FusedLocationProviderClient
   // private lateinit var possibleUserList:ArrayList<String>
    private val model: DigitalCovidCommonViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        //possibleUserList=ArrayList()
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        methodObj= Methods()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        progressDialog=methodObj.progressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        methodObj.progressDialogShow(progressDialog,"Please Wait! Setting the Application.")
        database = FirebaseDatabase.getInstance().getReference()
        val mapObserver=Observer<Boolean>{set ->
            if (set) {

                val statusObserver = Observer<String> { status ->
                    val nic=methodObj.readSharedPreferences("currentUserNic",sharedPreferences)
                    getInfectedUsers(mMap)
                    getPossibleOneUsers(mMap)
                    getPossibleTwoUsers(mMap)
                    if (status == "infected") {
                        profilesectionbackground_constraint_layout.background = ContextCompat.getDrawable(applicationContext, R.drawable.red_back)
                        profile_image_view.background = ContextCompat.getDrawable(applicationContext, R.drawable.red_profile_back)
                        safe_text_view.text = "(YOU ARE INFECTED)"
                    } else if (status == "possible1" && status != "infected") {
                        profilesectionbackground_constraint_layout.background = ContextCompat.getDrawable(applicationContext, R.drawable.orange_back)
                        profile_image_view.background = ContextCompat.getDrawable(applicationContext, R.drawable.orange_profile_back)

                        findPossibleUsersLevelOne(mMap,nic!!,"Me")
                        safe_text_view.text = "(YOU MAY BE INFECTED[1])"
                    } else if (status == "possible2" && status != "infected" && status != "possible1") {
                        profilesectionbackground_constraint_layout.background = ContextCompat.getDrawable(applicationContext, R.drawable.yellow_back)
                        profile_image_view.background = ContextCompat.getDrawable(applicationContext, R.drawable.yellow_profile_back)
                        safe_text_view.text = "(YOU MAY BE INFECTED[2])"
                        findPossibleUsersLevelTwo(mMap,nic!!)
                    }
                }
                model.userStatus.observe(this, statusObserver)
            }
        }
        model.mMapSet.observe(this,mapObserver)

        checkUserPossibleTwo()
        checkUserPossibleOne()
        checkUserInfected()
        if (mAuth.currentUser==null)
            this.signOut()
        else{
            this.getUserData()
        }
        if(!mAuth.currentUser!!.isEmailVerified){
            val dialogVerifyEmail = CustomAlertDialog(this, CustomAlertDialog.DialogStyle.FILL_STYLE)
            dialogVerifyEmail.setAlertMessage("A verification email has been sent to your email.Please login to your email account and follow the instructions given to verify your account.")
            dialogVerifyEmail.setDialogType(CustomAlertDialog.DialogType.ERROR)
            dialogVerifyEmail.setDialogImage(getDrawable(R.mipmap.ic_launcher_foreground),0);
            dialogVerifyEmail.setPositiveButton("Resend the Email") {
                sendVerificationEmail()
                dialogVerifyEmail.dismiss()
            }
            dialogVerifyEmail.setNegativeButton("Cancel") {

                dialogVerifyEmail.cancel()
            }

            dialogVerifyEmail.create();
            dialogVerifyEmail.show();
        }
        round_btn_pressed_li_button.setOnClickListener {
            this.onRoundBtnPressedLiPressed()
        }
        profile_image_view.setOnClickListener {
            this.signOut()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun checkUserPossibleTwo(){
        val nic=methodObj.readSharedPreferences("currentUserNic",sharedPreferences)

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val t=child.children
                    for (a in t){
                        if (nic==a.key.toString()) {
                            model.userStatus.value="possible2"
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("possible2").addValueEventListener(userListener)
    }
    private fun checkUserPossibleOne(){
        val nic=methodObj.readSharedPreferences("currentUserNic",sharedPreferences)

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val t=child.children
                    for (a in t){
                        //possibleUserList.add(a.key.toString())
                        if (nic==a.key.toString()) {

                            model.userStatus.value="possible1"
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("possible").addValueEventListener(userListener)
    }
    private fun checkUserInfected(){
        val nic=methodObj.readSharedPreferences("currentUserNic",sharedPreferences)
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    if (nic==child.value.toString()) {
                        model.userStatus.value="infected"
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("infected").addValueEventListener(userListener)

    }
    private fun getUserData(){

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    val user = child.getValue(User::class.java)
                    if (mAuth.currentUser?.email == user?.email){
                        john_smith_text_view.text= user!!.username
                        methodObj.addSharedPreference("currentUserNic",user.nic.toString(),sharedPreferences)
                        methodObj.progressDialogDismiss(progressDialog)

                        return
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                methodObj.progressDialogDismiss(progressDialog)
                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("users").addValueEventListener(userListener)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style))
            if (!success) {

            }
        } catch (e: Resources.NotFoundException) {

        }
        model.mMapSet.value=true
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    val colomco = LatLng(location!!.latitude, location.longitude)
                    //mMap.addMarker(MarkerOptions().position(colomco).title("Marker in Colombo"))

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(colomco))
                    mMap.animateCamera( CameraUpdateFactory.zoomTo( 12.0f ) )
                }

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
            mAuth.signOut()
            this.startWelcomeActivity()
            finish()
        }
    }

    private fun possibleUsersLevelOneDrawPath(mMap:GoogleMap,from:String,to:String,title:String){
        val userPossibleListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var latLngFrom:LatLng= LatLng(0.0,0.0)
                var latLngTo:LatLng=LatLng(0.0,0.0)
                for (child in dataSnapshot.children) {
                    val user=child.getValue(User::class.java)
                    if(user!!.nic==from)
                        latLngFrom=LatLng(user!!.lat,user.long)
                    if (user!!.nic==to)
                        latLngTo=LatLng(user!!.lat,user.long)
                }
                val line: Polyline = mMap.addPolyline(PolylineOptions()
                        .add(latLngFrom, latLngTo)
                        .width(5f)
                        .color(Color.RED))
                mMap.addMarker(MarkerOptions().position(latLngFrom).title(title).snippet("Level 1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                mMap.addMarker(MarkerOptions().position(latLngTo).title("Carrier"))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("users").addValueEventListener(userPossibleListener)
    }
    private fun getInfectedUsers(mMap: GoogleMap){
        val infetedUserList=ArrayList<String>()
        val infectedUserListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    infetedUserList.add(child.value.toString())
                }
                for (user in infetedUserList){
                    val userListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val infectedUser = dataSnapshot.getValue(User::class.java)
                            addZoneToMap(mMap,Color.RED,LatLng(infectedUser!!.lat,infectedUser.long))
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            val e=databaseError.toException()

                            TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                                    TastyToast.LENGTH_LONG, TastyToast.ERROR)
                        }
                    }
                    database.child("users").child(user).addValueEventListener(userListener)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("infected").addValueEventListener(infectedUserListener)
    }
    private fun getPossibleOneUsers(mMap: GoogleMap){
        val possibleUserList=ArrayList<String>()
        val possibleUserListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    for (c in child.children)
                        possibleUserList.add(c.key.toString())
                }
                for (user in possibleUserList){
                    val userListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val possibleUser = dataSnapshot.getValue(User::class.java)
                            addZoneToMap(mMap,Color.rgb(255,140,0),LatLng(possibleUser!!.lat,possibleUser.long))
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            val e=databaseError.toException()

                            TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                                    TastyToast.LENGTH_LONG, TastyToast.ERROR)
                        }
                    }
                    database.child("users").child(user).addValueEventListener(userListener)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("possible").addValueEventListener(possibleUserListener)
    }

    private fun getPossibleTwoUsers(mMap: GoogleMap){
        val possibleUserList=ArrayList<String>()
        val possibleUserListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child in dataSnapshot.children){
                    for (c in child.children)
                        possibleUserList.add(c.key.toString())
                }
                for (user in possibleUserList){
                    val userListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val possibleUser = dataSnapshot.getValue(User::class.java)
                            addZoneToMap(mMap,Color.YELLOW,LatLng(possibleUser!!.lat,possibleUser.long))
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            val e=databaseError.toException()

                            TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                                    TastyToast.LENGTH_LONG, TastyToast.ERROR)
                        }
                    }
                    database.child("users").child(user).addValueEventListener(userListener)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("possible2").addValueEventListener(possibleUserListener)
    }
    private fun findPossibleUsersLevelOne(mMap: GoogleMap,nic:String,title: String){

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (child in dataSnapshot.children) {
                    val t=child.children
                    for (a in t){
                        if (nic==a.key.toString()) {
                            val from=a.key.toString()
                            val to=a.value.toString()
                            possibleUsersLevelOneDrawPath(mMap, from, to,title)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("possible").addValueEventListener(userListener)
    }
    private fun findPossibleUsersLevelTwo(mMap: GoogleMap,nic:String){

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (child in dataSnapshot.children) {
                    val t=child.children
                    for (a in t){
                        if (nic==a.key.toString()) {
                            val from=a.key.toString()
                            val to=a.value.toString()
                            findPossibleUsersLevelOne(mMap,to,"Level 1 Carrier")
                            possibleUsersLevelTwoDrawPath(mMap, from, to)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("possible2").addValueEventListener(userListener)
    }

    private fun possibleUsersLevelTwoDrawPath(mMap: GoogleMap, from: String, to: String) {
        val userPossibleListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var latLngFrom:LatLng= LatLng(0.0,0.0)
                var latLngTo:LatLng=LatLng(0.0,0.0)
                for (child in dataSnapshot.children) {
                    val user=child.getValue(User::class.java)
                    if(user!!.nic==from)
                        latLngFrom=LatLng(user!!.lat,user.long)
                    if (user!!.nic==to)
                        latLngTo=LatLng(user!!.lat,user.long)
                }
                val line: Polyline = mMap.addPolyline(PolylineOptions()
                        .add(latLngFrom, latLngTo)
                        .width(5f)
                        .color(Color.YELLOW))
                mMap.addMarker(MarkerOptions().position(latLngFrom).title("Me").snippet("Level 2").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))

            }

            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()

                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.child("users").addValueEventListener(userPossibleListener)
    }

    private fun addZoneToMap(mMap: GoogleMap,color: Int,latLng: LatLng){
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(1000.0)
        circleOptions.fillColor(color)
        circleOptions.strokeColor(color)
        mMap.addCircle(circleOptions);
    }
    private fun sendVerificationEmail(){
        val user = FirebaseAuth.getInstance().currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        TastyToast.makeText(applicationContext, "Verification email resent.",
                                TastyToast.LENGTH_LONG, TastyToast.SUCCESS)

                    } else {
                        TastyToast.makeText(applicationContext, "Error sending verification email.Please try again later. ",
                                TastyToast.LENGTH_LONG, TastyToast.ERROR)
                    }
                }
    }
}
