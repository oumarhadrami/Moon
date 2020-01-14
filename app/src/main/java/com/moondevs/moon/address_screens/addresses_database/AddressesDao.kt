package com.moondevs.moon.address_screens.addresses_database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AddressesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address)

    @Update
    suspend fun update(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Query("select * from Addresses")
    fun getAll(): LiveData<List<Address>>

    @Query("select IFNULL(SUM(addressId),0) from Addresses")
    fun getCount(): LiveData<Int>


}