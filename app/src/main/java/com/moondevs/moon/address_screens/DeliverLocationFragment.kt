package com.moondevs.moon.address_screens


import android.app.Application
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moondevs.moon.R
import com.moondevs.moon.address_screens.addresses_database.Address
import com.moondevs.moon.address_screens.addresses_database.AddressViewModelFactory
import com.moondevs.moon.databinding.FragmentDeliverLocationBinding
import com.moondevs.moon.util.FirestoreUtil
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class DeliverLocationFragment : Fragment() , OnMapReadyCallback {

    private lateinit var binding : FragmentDeliverLocationBinding
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var appBar : AppBarLayout
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var viewModel: AddressViewModel
    private lateinit var args : DeliverLocationFragmentArgs


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_deliver_location,container,false)

        // initialize viewModel
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = AddressViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AddressViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        //visibility of the AppBar and BottomNavView
        appBar = activity!!.findViewById(R.id.appbar)
        bottomNav = activity!!.findViewById(R.id.nav_view)
        appBar.visibility = View.GONE
        bottomNav.visibility = View.GONE

        //visibility of confirm location and update address buttons
        args = DeliverLocationFragmentArgs.fromBundle(arguments!!)
        if (args.flag == 0)
            binding.updateAddress.visibility = View.GONE
        else {
            binding.confirmLocationButton.visibility = View.GONE
            binding.phoneNumberAddressTextfield.setText(args.phoneNumber.substring(4))
            binding.nameAddressTextfield.setText(args.name)
            binding.updateAddress.setOnClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.update(getUpdatedAddress())
                }
                it.findNavController().navigate(DeliverLocationFragmentDirections.actionDeliverLocationFragmentToAddressListFragment())
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //initializing fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        return binding.root
    }

    private fun getUpdatedAddress(): Address {
        return Address(
            addressId = args.addressId,
            Name = args.name,
            PhoneNumber = args.phoneNumber,
            Latitude = binding.latLngTextview.text.toString().substringBeforeLast(","),
            Longitude = binding.latLngTextview.text.toString().substringAfterLast(", ")
        )
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
            binding.progressBarInMap.visibility = View.INVISIBLE
            binding.latLngTextview.visibility = View.VISIBLE
            val center = map.cameraPosition.target
            val newMarker = MarkerOptions().position(center).title("New Position")
            val newLatLng =  newMarker.position
            val latitude = newLatLng.latitude
            val longitude = newLatLng.longitude
            binding.latLngTextview.text = "$latitude , $longitude"


            /** Handle confirm button click*/
            binding.confirmLocationButton.setOnClickListener {
                /**  add conditions for UI*/
                val phoneNumberString = binding.phoneNumberAddressTextfield.text.toString().trim()
                val nameString = binding.nameAddressTextfield.text.toString().trim()
                if (phoneNumberString.isEmpty() || phoneNumberString.length < 8){
                    binding.phoneNumberAddress.error = "please enter the 8-digit phone number!"
                    binding.phoneNumberAddress.requestFocus()
                    return@setOnClickListener
                }
                else binding.phoneNumberAddress.error = null

                if (nameString.isEmpty()){
                    binding.nameAddress.error = "please enter your name!"
                    binding.nameAddress.requestFocus()
                    return@setOnClickListener
                }
                else binding.phoneNumberAddress.error = null


                /** Get Address entered from UI*/
                val address = Address(
                    Name = nameString,
                    PhoneNumber = "+222$phoneNumberString",
                    Latitude = latitude.toString(),
                    Longitude = longitude.toString()
                )

                /** Insert the address in database and firestore*/
                viewModel.viewModelScope.launch {
                    viewModel.insert(address)
                    binding.progressBarAddress.visibility = View.VISIBLE
                    val rowId = viewModel.getLastAddedAddressId()
                    FirestoreUtil.insertAddress(address, rowId)
                }
                /** Let UI thread sleep for 3 seconds while showing progressBar */
                findNavController().navigate(DeliverLocationFragmentDirections.actionDeliverLocationFragmentToNavigationCart())


            }
        }
        //handling the camera moving mode
        map.setOnCameraMoveListener {
            binding.progressBarInMap.visibility = View.VISIBLE
            binding.latLngTextview.visibility = View.INVISIBLE
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

    //make appBar and BottomNav visible outside this fragment
    override fun onDestroyView() {
        super.onDestroyView()
        appBar.visibility = View.VISIBLE
        bottomNav.visibility = View.VISIBLE
    }




}
