package com.moondevs.moon.home_screens.account.adresses


import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moondevs.moon.R
import com.moondevs.moon.home_screens.account.adresses.addresses_database.AddressViewModel
import com.moondevs.moon.home_screens.account.adresses.addresses_database.AddressViewModelFactory
import com.moondevs.moon.databinding.FragmentAddressListBinding


class AddressListFragment : Fragment() {

    private lateinit var binding : FragmentAddressListBinding
    private lateinit var viewModel : AddressViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_address_list, container, false)

        /**initialize FavoriteShopsViewModel*/
        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory =
            AddressViewModelFactory(
                application
            )
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(AddressViewModel::class.java)

        /**linking adapter to the recyclerview*/
        val adapter = AddressesAdapter(context!!, viewModel)
        binding.addressesList.adapter = adapter
        viewModel.allAddresses.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        /**setting the visibility of the layout depending on count of addresses*/
        viewModel.allAddressesCount.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                binding.emptyAddressesContainer.visibility = View.VISIBLE
            }
            else{
                binding.emptyAddressesContainer.visibility = View.GONE
            }
        })

        return binding.root
    }


}
