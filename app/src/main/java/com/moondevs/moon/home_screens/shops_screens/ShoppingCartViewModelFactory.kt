package com.moondevs.moon.home_screens.shops_screens

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShoppingCartViewModelFactory (private val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ShoppingCartViewModel::class.java)){
            return ShoppingCartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
