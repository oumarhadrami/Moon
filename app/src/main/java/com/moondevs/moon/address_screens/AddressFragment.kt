package com.moondevs.moon.address_screens


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.moondevs.moon.AddressFragmentArgs
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentAddressBinding


class AddressFragment : Fragment() {
    private lateinit var binding : FragmentAddressBinding
    private lateinit var args : AddressFragmentArgs
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_address, container, false)

        binding.latLngAddressTextfield.isEnabled = false

        args = AddressFragmentArgs.fromBundle(arguments!!)
        binding.latLngAddressTextfield.setText(args.latitude + " , " + args.longitude)

        return binding.root
    }


}
