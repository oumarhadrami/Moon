package com.moondevs.moon.home_screens.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var binding : FragmentCartBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart,container,false)
        cartViewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        cartViewModel.text.observe(this, Observer {
            binding.textCart.text = it
        })
        return binding.root
    }
}