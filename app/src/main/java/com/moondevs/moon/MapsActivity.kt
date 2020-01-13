package com.moondevs.moon

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.DisplayMetrics
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlin.math.roundToInt

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var confirmLocationButton : MaterialButton
    private lateinit var latLngTextView: MaterialTextView
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initializing UI components
        latLngTextView = findViewById(R.id.lat_lng_textview)
        progressBar = findViewById(R.id.progressBarInMap)
        confirmLocationButton = findViewById(R.id.confirm_location_button)

        //initializing fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Set Padding for the map for myLocation Button and enable maps location and move camera to user location
        map.setPadding(0,dpToPx(30,this),0,0)
        map.isMyLocationEnabled = true
        moveToUserLocation()

        // handle camera in rest mode
        map.setOnCameraIdleListener {
            progressBar.visibility = View.GONE
            latLngTextView.visibility = View.VISIBLE
            val center = map.cameraPosition.target
            val newMarker = MarkerOptions().position(center).title("New Position")
            val newLatLng =  newMarker.position
            latLngTextView.text = "${newLatLng.latitude} , ${newLatLng.longitude}"
        }

        map.setOnCameraMoveListener {
            progressBar.visibility = View.VISIBLE
            latLngTextView.visibility = View.GONE
        }



    }
    // convert dp to pixels units
    fun dpToPx(dp: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    // animate camera to user's location
    fun moveToUserLocation()  {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                //getting latitude and longitude and animating camera to user's location
                val userLatLng = LatLng(location!!.latitude, location.longitude)
                val zoomLevel = 18f
                val userLocation = CameraUpdateFactory.newLatLngZoom(userLatLng,zoomLevel)
                map.animateCamera(userLocation)
            }
    }
}
