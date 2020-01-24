package com.moondevs.moon.address_screens

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moondevs.moon.address_screens.addresses_database.Address
import com.moondevs.moon.home_screens.account.AccountDatabase
import com.moondevs.moon.address_screens.addresses_database.AddressesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AddressViewModel (application: Application): ViewModel(){
    private val repo : AddressesRepository
    val allAddresses : LiveData<List<Address>>
    val allAddressesCount : LiveData<Int>


    init {
        val dao = AccountDatabase.getDatabase(application).addressesDao
        repo = AddressesRepository(dao)
        allAddresses = repo.allAddresses
        allAddressesCount = repo.allAddressesCount
    }

    suspend fun insert(address: Address) = viewModelScope.launch {
        repo.insert(address)
    }

    suspend fun update(address: Address) = viewModelScope.launch {
        repo.update(address)
    }

    suspend fun deleteAddress(addressId : Long) = viewModelScope.launch {
        repo.deleteAddress(addressId)
    }

    suspend fun getLastAddedAddress() : Address{
        val currentAddress = viewModelScope.async {
            repo.getLastAddedAddress()
        }
        return currentAddress.await()
    }

    suspend fun getLastAddedAddressId() : Long{
        val rowId = viewModelScope.async {
            repo.getLastAddedAddressId()
        }
        return rowId.await()
    }

    suspend fun getAddressesSizeNonLiveData() : Int{
        val totalSize = viewModelScope.async {
            repo.getAddressesSizeNonLiveData()
        }
        return totalSize.await()
    }

}