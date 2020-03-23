package com.company_name.digital_covid19.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company_name.digital_covid19.R
import com.company_name.digital_covid19.methods.Methods
import com.company_name.digital_covid19.models.User
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sdsmdg.tastytoast.TastyToast
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        sharedPreferences=this.getSharedPreferences("digitalCovidPrefs",0)
        methodObj= Methods()
        progressDialog=methodObj.progressDialog(this)
        mAuth = FirebaseAuth.getInstance()
        val nic=methodObj.readSharedPreferences("currentUserNic",sharedPreferences)
        methodObj.progressDialogShow(progressDialog,"Please Wait! Setting the Application.")
        database = FirebaseDatabase.getInstance().getReference("users/$nic")
        if (mAuth.currentUser==null)
            this.signOut()
        else{
            this.getUserData()

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
    private fun getUserData(){
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)
                john_smith_text_view.text= user!!.username
                methodObj.progressDialogDismiss(progressDialog)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                val e=databaseError.toException()
                methodObj.progressDialogDismiss(progressDialog)
                TastyToast.makeText(applicationContext, "Error occured while reading database.$e",
                        TastyToast.LENGTH_LONG, TastyToast.ERROR)
            }
        }
        database.addValueEventListener(userListener)
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style))
            if (!success) {

            }
        } catch (e: Resources.NotFoundException) {

        }
        // Add a marker in Sydney and move the camera
        val colomco = LatLng(6.927079, 79.861244)
        mMap.addMarker(MarkerOptions().position(colomco).title("Marker in Colombo"))

        mMap.moveCamera(CameraUpdateFactory.newLatLng(colomco))
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 12.0f ) )
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

}
