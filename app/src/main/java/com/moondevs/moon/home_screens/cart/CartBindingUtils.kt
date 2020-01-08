package com.moondevs.moon.home_screens.cart

import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.moondevs.moon.home_screens.shops_screens.cart_database.CartItem



@BindingAdapter("itemName")
fun MaterialTextView.setItemName(item : CartItem?){
    item?.let {
        text = item.shopItemName
    }
}



