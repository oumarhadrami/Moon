package com.moondevs.moon.home_screens.cart

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.moondevs.moon.R
import com.moondevs.moon.databinding.FragmentCartBinding
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModel
import com.moondevs.moon.home_screens.shops_screens.ShoppingCartViewModelFactory
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private lateinit var viewModel: ShoppingCartViewModel
    private lateinit var binding : FragmentCartBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart,container,false)

        val application : Application = requireNotNull(this).activity!!.application
        val viewModelFactory = ShoppingCartViewModelFactory(application)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ShoppingCartViewModel::class.java)
        binding.viewModel = viewModel




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