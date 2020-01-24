package com.moondevs.moon.home_screens.account.adresses.addresses_database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moondevs.moon.home_screens.account.AccountDatabase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class AddressViewModel (application: Application): ViewModel(){
    private val repo : AddressesRepository
    val allAddresses : LiveData<List<Address>>
    val allAddressesCount : LiveData<Int>


    init {
        val dao = AccountDatabase.getDatabase(application).addressesDao
        repo =
            AddressesRepository(
                dao
            )
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

    suspend fun getLastAddedAddress() : Address {
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

    suspend fun getAllAddressesNonLiveData() : List<Address>{
        val allAddresses = viewModelScope.async {
            repo.getAllAddressesNonLiveData()
        }
        return allAddresses.await()
    }
    
    suspend fun updateSelectedAddress(item : Address){
        viewModelScope.launch {
            update(
                Address(
                    addressId = item.addressId,
                    Name = item.Name,
                    PhoneNumber = item.PhoneNumber,
                    Latitude = item.Latitude,
                    Longitude = item.Longitude,
                    isThisTheSelectedAddress = true
                )
            )
            Timber.i("Well ${getAllAddressesNonLiveData()}")
            for (addressItem in getAllAddressesNonLiveData()) {
                if (addressItem.addressId != item.addressId) {
                    update(
                        Address(
                            addressId = addressItem.addressId,
                            Name = addressItem.Name,
                            PhoneNumber = addressItem.PhoneNumber,
                            Latitude = addressItem.Latitude,
                            Longitude = addressItem.Longitude,
                            isThisTheSelectedAddress = false
                        )
                    )
                    Timber.i("Not Equal")
                }
            }
        }
    }

    private suspend fun getAddedAddressWithId(rowId: Long): Address {
        val addressWithThisId = viewModelScope.async { 
            repo.getAddresWithThisId(rowId)
        }
        return addressWithThisId.await()
    }

}