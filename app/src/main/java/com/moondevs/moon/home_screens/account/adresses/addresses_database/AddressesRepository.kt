package com.moondevs.moon.home_screens.account.adresses.addresses_database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.moondevs.moon.home_screens.account.adresses.addresses_database.Address
import com.moondevs.moon.home_screens.account.adresses.addresses_database.AddressesDao

class AddressesRepository (private val addressesDao: AddressesDao){

    val allAddressesCount : LiveData<Int> = addressesDao.getCount()
    val allAddresses : LiveData<List<Address>> = addressesDao.getAll()

    @WorkerThread
    suspend fun insert(address: Address){
        addressesDao.insert(address)
    }

    @WorkerThread
    suspend fun update(address: Address){
        addressesDao.update(address)
    }

    @WorkerThread
    suspend fun deleteAddress(addressId : Long){
        addressesDao.deleteAddress(addressId)
    }

    @WorkerThread
    suspend fun getLastAddedAddress() : Address {
        return addressesDao.getLastAddedAddress()
    }

    @WorkerThread
    suspend fun getLastAddedAddressId() : Long{
        val address = addressesDao.getLastAddedAddress()
        return address.addressId
    }

    @WorkerThread
    suspend fun getAddressesSizeNonLiveData(): Int {
        return addressesDao.getAddressesSizeNonLiveData()
    }

    @WorkerThread
    suspend fun getAllAddressesNonLiveData(): List<Address> {
        return addressesDao.getAllAddressesNonLiveData()
    }

    @WorkerThread
    suspend fun getAddresWithThisId(rowId: Long): Address {
        return addressesDao.getAddressWithThisId(rowId)
    }


}