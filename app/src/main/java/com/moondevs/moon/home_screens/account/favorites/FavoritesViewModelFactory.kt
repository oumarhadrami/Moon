package com.moondevs.moon.home_screens.account.favorites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FavoritesViewModelFactory (private val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavoritesViewModel::class.java)){
            return FavoritesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}