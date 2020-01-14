package com.moondevs.moon.home_screens.cart

import android.Manifest
import android.annotation.TargetApi
import android.app.Application
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.moondevs.moon.BuildConfig
import com.moondevs.moon.R
import com.moondevs.moon.address_screens.AddressViewModel
import com.moondevs.moon.address_screens.addresses_database.AddressViewModelFactory
import com.moondevs.moon.databinding.FragmentCartBinding
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.address_layout.view.*
import kotlinx.coroutines.launch
import timber.log.Timber

class CartFragment : Fragment() {

    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var addressViewModel: AddressViewModel
    private lateinit var binding : FragmentCartBinding
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //initializing components
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart,container,false)

        //initialize ShoppingCartViewModel
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = activity

        //Initialize AddressViewModel
        val addressViewModelFactory = AddressViewModelFactory(application)
        addressViewModel = ViewModelProviders.of(activity!!, addressViewModelFactory).get(AddressViewModel::class.java)


        //linking adapter to the recyclerview
        val adapter = CartAdapter(viewModel,activity)
        binding.cartItemsList.adapter = adapter
        viewModel.allCartItems.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        //setting the visibility of the layouts in toolbar and page depending on cart total items
        viewModel.allItemsCount.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                activity!!.shop_details_in_cart_layout.visibility = View.GONE
                binding.cartLayout.visibility = View.GONE
            }
            else{
            binding.cartLayout.visibility = View.VISIBLE
            activity!!.shop_details_in_cart_layout.visibility = View.VISIBLE
        }
        })


        //asking for location permission if not granted and turning on gps buttoon if not enabled
        binding.setDeliveryLocation.setOnClickListener {
            if (foregroundAndBackgroundLocationPermissionApproved()) {
                checkDeviceLocationSettingsAndTurningGpsButtonOn()
            }
            else
            requestForegroundAndBackgroundLocationPermissions()
        }

        //set the visibility of addressContainer, placeOrder, setDeliveryLocation Views based on number of addresses
        addressViewModel.allAddressesCount.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                binding.addressContainer.visibility = View.GONE
                binding.placeOrder.visibility = View.GONE
                binding.setDeliveryLocation.visibility = View.VISIBLE
            }

            else{
                binding.addressContainer.visibility = View.VISIBLE
                binding.placeOrder.visibility = View.VISIBLE
                binding.setDeliveryLocation.visibility = View.GONE
            }
        })

        //edit address button click
        binding.addressContainer.add_address.setOnClickListener {
            findNavController().navigate(CartFragmentDirections.actionNavigationCartToDeliverLocationFragment())
        }






        return binding.root
    }




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
                findNavController().navigate(CartFragmentDirections.actionNavigationCartToDeliverLocationFragment())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettingsAndTurningGpsButtonOn(false)
        }
    }


    override fun onStart() {
        super.onStart()

        val shopDetailsInCartLayout = activity!!.findViewById<View>(R.id.shop_details_in_cart_layout)
        val shopImageInCart = shopDetailsInCartLayout.findViewById<ImageView>(R.id.shop_image_in_cart)
        val shopNameInCart = shopDetailsInCartLayout.findViewById<TextView>(R.id.shop_name_in_cart)
        //add content for toolbar

        viewModel.allItemsCount.observe(viewLifecycleOwner, Observer {totalItemsCount ->
            viewModel.viewModelScope.launch {
                if (totalItemsCount > 0) {
                    val shopNameInCartString = viewModel.getShopNameFromDB()
                    val shopImageInCartString = viewModel.getShopImageFromDB()
                    val shopRefInCart = viewModel.getShopRefFromDB()
                    shopDetailsInCartLayout.setOnClickListener {
                        findNavController().navigate(CartFragmentDirections.actionNavigationCartToShopFragment(shopRefInCart,shopNameInCartString,shopImageInCartString))
                    }
                    Glide.with(shopDetailsInCartLayout.context)
                        .load(shopImageInCartString)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                        )
                        .into(shopImageInCart)
                    shopNameInCart.text = shopNameInCartString
                    shopDetailsInCartLayout.visibility = View.VISIBLE
                }
            }
        })
    }



    override fun onStop() {
        super.onStop()
        val shopDetailsInCartLayout = activity!!.findViewById<View>(R.id.shop_details_in_cart_layout)
        shopDetailsInCartLayout.visibility = View.GONE

    }
}
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29