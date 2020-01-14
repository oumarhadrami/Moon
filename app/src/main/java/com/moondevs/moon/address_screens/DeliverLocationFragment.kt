package com.moondevs.moon.address_screens


import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentDeliverLocationBinding
import kotlin.math.roundToInt

class DeliverLocationFragment : Fragment() , OnMapReadyCallback {

    private lateinit var binding : FragmentDeliverLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var appBar : AppBarLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_deliver_location,container,false)


        //visibility of the appbar
        appBar = activity!!.findViewById(R.id.appbar)
        appBar.visibility = View.GONE

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //initializing fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        return binding.root
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
        map.setPadding(0,dpToPx(30,activity!!),0,0)
        map.isMyLocationEnabled = true
        moveToUserLocation()

        // handle camera in rest mode
        map.setOnCameraIdleListener {
            binding.progressBarInMap.visibility = View.GONE
            binding.latLngTextview.visibility = View.VISIBLE
            val center = map.cameraPosition.target
            val newMarker = MarkerOptions().position(center).title("New Position")
            val newLatLng =  newMarker.position
            val latitude = newLatLng.latitude
            val longitude = newLatLng.longitude
            binding.latLngTextview.text = "${newLatLng.latitude} , ${newLatLng.longitude}"
            binding.confirmLocationButton.setOnClickListener {
                findNavController().navigate(
                    DeliverLocationFragmentDirections.actionDeliverLocationFragmentToAddressFragment(
                        latitude.toString(),
                        longitude.toString()
                    )
                )
            }
        }

        map.setOnCameraMoveListener {
            binding.progressBarInMap.visibility = View.VISIBLE
            binding.latLngTextview.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        appBar.visibility = View.VISIBLE
    }


}
