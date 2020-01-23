package com.moondevs.moon.live_tracking_screens


import android.Manifest
import android.annotation.TargetApi
import android.app.Application
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.moondevs.moon.BuildConfig
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentLiveTrackingBinding
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import kotlinx.coroutines.launch
import timber.log.Timber


class LiveTrackingFragment : Fragment() {
    private lateinit var binding : FragmentLiveTrackingBinding
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var appBar : AppBarLayout
    private lateinit var bottomNav : BottomNavigationView
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q


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
            findNavController().navigate(LiveTrackingFragmentDirections.actionLiveTrackingFragmentToNavigationAccount())
        }

        return binding.root
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