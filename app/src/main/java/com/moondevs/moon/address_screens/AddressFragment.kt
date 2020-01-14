package com.moondevs.moon.address_screens


import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.moondevs.moon.R
import com.moondevs.moon.address_screens.addresses_database.Address
import com.moondevs.moon.address_screens.addresses_database.AddressViewModelFactory
import com.moondevs.moon.databinding.FragmentAddressBinding
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


class AddressFragment : Fragment() {

    private lateinit var binding : FragmentAddressBinding
    private lateinit var args : AddressFragmentArgs
    private lateinit var viewModel: AddressViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_address, container, false)

        // initialize viewModel
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = AddressViewModelFactory(application)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AddressViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //make the location edittext uneditable
        binding.latLngAddressTextfield.isEnabled = false

        //get Location from Arguments
        args = AddressFragmentArgs.fromBundle(arguments!!)
        binding.latLngAddressTextfield.setText(args.latitude + " , " + args.longitude)

        //handle the submit Button Click
        binding.submitAddress.setOnClickListener {
            val phoneNumberString = binding.phoneNumberAddressTextfield.text.toString().trim()
            val nameString = binding.nameAddressTextfield.text.toString().trim()
            if (phoneNumberString.isEmpty() || phoneNumberString.length < 8){
                binding.phoneNumberAddress.error = "please enter the 8-digit phone number!"
                binding.phoneNumberAddress.requestFocus()
                return@setOnClickListener
            }
            if (nameString.isEmpty()){
                binding.nameAddress.error = "please enter your name!"
                binding.nameAddress.requestFocus()
                return@setOnClickListener
            }

            val address = Address(
                Name = nameString,
                PhoneNumber = "+222$phoneNumberString",
                Latitude = args.latitude,
                Longitude = args.longitude
            )

            viewModel.viewModelScope.launch { viewModel.insert(address)}
            binding.progressBarAddress.visibility = View.VISIBLE
            thread {
                Thread.sleep((1 * 1000).toLong())
            }.priority = Thread.NORM_PRIORITY





        }

        return binding.root
    }


}
