package com.moondevs.moon.live_tracking_screens


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.moondevs.moon.BuildConfig
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentLiveTrackingBinding
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import com.moondevs.moon.util.FirestoreUtil
import timber.log.Timber


const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 101
class LiveTrackingFragment : Fragment()  , OnMapReadyCallback {
    private lateinit var binding : FragmentLiveTrackingBinding
    private lateinit var map: GoogleMap
    private lateinit var args: LiveTrackingFragmentArgs
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var appBar : AppBarLayout
    private lateinit var bottomNav : BottomNavigationView
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private val runningMOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M


    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_live_tracking,container,false)

        /**initialize ShoppingCartViewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        shoppingCartViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)

        /**visibility of the AppBar and BottomNavView*/
        appBar = activity!!.findViewById(R.id.appbar)
        bottomNav = activity!!.findViewById(R.id.nav_view)
        appBar.visibility = View.GONE
        bottomNav.visibility = View.GONE



        /**Making empty textview size of statusBar*/
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            getStatusBarHeight()
        )
        binding.statusbarTextview.layoutParams = params


        /**check permission*/
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndTurningGpsButtonOn()
        }
        else {
            requestForegroundAndBackgroundLocationPermissions()
        }


        /**Navigating back after click the close button*/
        binding.closeTracking.setOnClickListener {
            findNavController().navigateUp()
        }

        /** Obtain the SupportMapFragment and get notified when the map is ready to be used.*/
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_live) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /**initializing fusedLocationClient*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        /**Get the args*/
        args = LiveTrackingFragmentArgs.fromBundle(arguments!!)
        binding.orderId.text = "#${args.orderId}"
        binding.orderDateItemsAmount.text = "${args.orderDate} | ${args.orderTotalItemsCount} ${getString(R.string.items)} | ${args.orderAmountToPay} MRU"


        /**if delivery agent is assigned, his info is displayed*/
        FirestoreUtil.firestoreInstance.collection("CurrentOrders").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Timber.i("listen:error $e")
                return@addSnapshotListener
            }

            for (document in snapshots!!.documents) {
                if (document.get("orderId").toString() == args.orderId) {
                    if (document.getBoolean("isOrderAssigned")!!) {
                        displayDeliveryAgentInfo(document["deliveryAgent"].toString())
                        break
                    }
                }
            }
        }







            return binding.root
    }

    private fun displayDeliveryAgentInfo(deliveryAgentID: String) {
        FirestoreUtil.firestoreInstance.collection("Agents").document(deliveryAgentID)
            .get()
            .addOnSuccessListener {documentSnapshot ->
                binding.deliveryAgentName.text = documentSnapshot["Name"].toString()
                binding.deliveryAgentInfoContainer.visibility = View.VISIBLE

                //TODO Request Permission for phone call

                binding.callDeliveryAgent.setOnClickListener {
                   if (phoneCallPermissionGranted()) {
                       val intent = Intent(Intent.ACTION_CALL)
                       intent.data = Uri.parse("tel:" + documentSnapshot["phoneNumber"].toString())
                       startActivity(intent)
                   }
                    else {
                       requestPhoneCallPermission()
                   }
                }
            }
    }

    private fun phoneCallPermissionGranted(): Boolean {
        return (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.CALL_PHONE))
    }


    private fun requestPhoneCallPermission() {
        // Here, thisActivity is the current activity
        if (phoneCallPermissionGranted())
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningMOrLater -> {
                permissionsArray += Manifest.permission.CALL_PHONE
                CALL_PHONE_PERMISSION_RESULT_CODE
            }
            else -> CALL_PHONE_PERMISSIONS_REQUEST_CODE
        }
        Timber.d("Request foreground only location permission")
        ActivityCompat.requestPermissions(
            activity!!,
            permissionsArray,
            resultCode
        )
    }



    /**make appBar and BottomNav visible outside this fragment*/
    override fun onDestroyView() {
        super.onDestroyView()
        appBar.visibility = View.VISIBLE
        bottomNav.visibility = View.VISIBLE
    }

    private fun getStatusBarHeight(): Int {
        val height: Int
        val myResources: Resources = resources
        val idStatusBarHeight: Int =
            myResources.getIdentifier("status_bar_height", "dimen", "android")
        height = if (idStatusBarHeight > 0) {
            resources.getDimensionPixelSize(idStatusBarHeight)
        } else {
            0
        }
        return height
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

        // Enable maps location and move camera to user location
        map.isMyLocationEnabled = true
        showLocationsOfUserAndShop()



    }





    // animate camera to user's location
    private fun showLocationsOfUserAndShop()  {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                //getting latitude and longitude and animating camera to user's location
                val userLatLng = LatLng(location!!.latitude, location.longitude)
                map.addMarker(MarkerOptions().position(userLatLng).icon(bitmapDescriptorFromVector(context!!, R.drawable.ic_marker_user)))
                FirestoreUtil.firestoreInstance.document(args.orderDoc).get().addOnSuccessListener {orderDoc ->
                    FirestoreUtil.firestoreInstance.document("Shops/${orderDoc.get("shopId")}").get().addOnSuccessListener {shopDoc->
                        val shopLat = shopDoc.get("shopLatitude")!!.toString().toDouble()
                        val shopLng = shopDoc.get("shopLongitude")!!.toString().toDouble()
                        val shopLatLng = LatLng(shopLat, shopLng)
                        map.addMarker(MarkerOptions().position(shopLatLng).icon(bitmapDescriptorFromVector(context!!, R.drawable.ic_marker_shop)))
                        zoomtoBothLocations(userLatLng, shopLatLng)
                    }
                }
            }
    }


    private fun zoomtoBothLocations(
        userLatLng: LatLng,
        shopLatLng: LatLng
    ) {
        val builder = LatLngBounds.builder()
        builder.include(userLatLng)
        builder.include(shopLatLng)
        val bounds = builder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 180)
        map.animateCamera(cameraUpdate)
    }




    private fun bitmapDescriptorFromVector(context: Context, vectorDrawable: Int) : BitmapDescriptor {
        val background = ContextCompat.getDrawable(context, vectorDrawable)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(background.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
























            /**Handling the approval of foreground and background permissions (background in case of using android Q or later)*/
    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            activity!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }


    /**Requesting foreground and background permissions (background in case of using android Q or later)*/
    @TargetApi(29 )
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Timber.d("Request foreground only location permission")
        ActivityCompat.requestPermissions(
            activity!!,
            permissionsArray,
            resultCode
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Timber.d("onRequestPermissionResult")

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED))
        {
            Snackbar.make(
                binding.root,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            // permission granted and user can open the map
            //findNavController().navigate(CartFragmentDirections.actionNavigationCartToDeliveryLocationActivity())
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.phone_permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return
        }

        // Add other 'when' lines to check for other
        // permissions this app might request.



    }


    /**Turning the gps of the device in case it's off*/
    private fun checkDeviceLocationSettingsAndTurningGpsButtonOn(resolve : Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(activity!!)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        activity,
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("Error getting location settings resolution: %s", sendEx.message)
                }
            }
            else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndTurningGpsButtonOn()
                }.show()
            }

        }

        locationSettingsResponseTask.addOnCompleteListener {
            if ( it.isSuccessful ) {
                ////////TODO very important
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndTurningGpsButtonOn(false)
        }
    }








}
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
private const val CALL_PHONE_PERMISSION_RESULT_CODE = 35
private const val CALL_PHONE_PERMISSIONS_REQUEST_CODE = 36