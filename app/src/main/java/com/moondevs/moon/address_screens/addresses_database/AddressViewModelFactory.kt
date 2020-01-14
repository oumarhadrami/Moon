package com.moondevs.moon.address_screens.addresses_database

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moondevs.moon.address_screens.AddressViewModel

class AddressViewModelFactory (private val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddressViewModel::class.java)){
            return AddressViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}