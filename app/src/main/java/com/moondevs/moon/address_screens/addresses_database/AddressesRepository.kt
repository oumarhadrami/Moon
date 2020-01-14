package com.moondevs.moon.address_screens.addresses_database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

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
    suspend fun deleteAddress(address: Address){
        addressesDao.deleteAddress(address)
    }



}