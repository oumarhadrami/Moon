package com.moondevs.moon.home_screens.account.orders_list

import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import com.moondevs.moon.home_screens.cart.order_database.CurrentOrder

@BindingAdapter("shopName")
fun MaterialTextView.setShopName(item : CurrentOrder?){
    item?.let {
        text = item.shopName
    }
}

@BindingAdapter("amountToPay")
fun MaterialTextView.setAmountToPay(item : CurrentOrder?){
    item?.let {
        text = item.amountToPay.toString()
    }
}

@BindingAdapter("totalItemsCount")
fun MaterialTextView.setTotalItemsCount(item : CurrentOrder?){
    item?.let {
        text = item.totalItemsCount.toString()
    }
}

@BindingAdapter("orderDate")
fun MaterialTextView.setOrderDate(item : CurrentOrder?){
    item?.let {
        text = item.orderDate
    }
}