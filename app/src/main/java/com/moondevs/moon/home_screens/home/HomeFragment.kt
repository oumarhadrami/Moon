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
import com.google.firebase.firestore.FirebaseFirestore
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentHomeBinding


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


        return binding.root
    }


    override fun onStart() {
        super.onStart()
        //add content for toolbar
        val frameLayout = activity!!.findViewById<View>(R.id.toolbar_framelayout)
        frameLayout.visibility = View.GONE
        FirebaseFirestore.getInstance().clearPersistence()
    }


}