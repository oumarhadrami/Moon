package com.moondevs.moon.home_screens.cart

import android.Manifest
import android.annotation.TargetApi
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.moondevs.moon.BuildConfig
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentCartBinding
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.launch
import timber.log.Timber

class CartFragment : Fragment() {

    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var binding : FragmentCartBinding
    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart,container,false)

        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(ShoppingCartViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = activity

        val adapter = CartAdapter(viewModel,activity)
        binding.cartItemsList.adapter = adapter
        viewModel.allCartItems.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

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


        binding.setDeliveryLocation.setOnClickListener {
            if (foregroundAndBackgroundLocationPermissionApproved())
                //findNavController().navigate(CartFragmentDirections.actionNavigationCartToDeliveryLocationActivity())
            else
            requestForegroundAndBackgroundLocationPermissions()
        }







        return binding.root
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
}
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29