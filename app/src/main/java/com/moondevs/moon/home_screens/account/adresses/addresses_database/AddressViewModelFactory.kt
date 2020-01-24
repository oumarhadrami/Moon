package com.moondevs.moon.home_screens.account.adresses.addresses_database

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddressViewModelFactory (private val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddressViewModel::class.java)){
            return AddressViewModel(
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}