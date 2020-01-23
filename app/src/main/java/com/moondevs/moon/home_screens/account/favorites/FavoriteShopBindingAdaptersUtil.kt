package com.moondevs.moon.home_screens.account.favorites

import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import com.moondevs.moon.home_screens.account.favorites.database.FavoriteShop

@BindingAdapter("favoriteShopName")
fun MaterialTextView.setShopName(item : FavoriteShop?){
    item?.let {
        text = item.shopName
    }
}