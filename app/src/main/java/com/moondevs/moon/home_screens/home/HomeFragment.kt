package com.moondevs.moon.home_screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentHomeBinding


const val MY_PERMISSIONS_REQUEST_LOCATION = 101
class HomeFragment : Fragment() {
    private lateinit var auth :FirebaseAuth

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        //handle the back button behavior after login
        /*auth = FirebaseAuth.getInstance()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (auth.currentUser != null)
                requireActivity().finish()
         }

         */


        //FirestoreUtil.updateCurrentUser(name = "Hadramy")

        binding.supermarketsLogo.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToShopsListFragment("Supermarkets"))
        }

        binding.restaurantsLogo.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToShopsListFragment("Restaurants"))
        }

        binding.laitieresLogo.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToShopsListFragment("Laitieres"))
        }

        binding.bakeriesLogo.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToShopsListFragment("Bakeries"))
        }

        binding.pharmaciesLogo.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToShopsListFragment("Pharmacies"))
        }

        binding.boutiquesLogo.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToShopsListFragment("Boutiques"))
        }


        //request Location Permission
        //getUserLocation()
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        //add content for toolbar
        val locationLayout = activity!!.findViewById<View>(R.id.location_layout)
        locationLayout.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        val locationLayout = activity!!.findViewById<View>(R.id.location_layout)
        locationLayout.visibility = View.GONE

    }


    /*private fun getUserLocation() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(binding.root.context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                }!!) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

        }

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Snackbar.make(binding.root, "Retrieving your location..", Snackbar.LENGTH_SHORT).show()
                    //get Longtitude and Latitude
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Snackbar.make(binding.root, "Please enable location in settings!", Snackbar.LENGTH_SHORT).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

     */
}