package com.moondevs.moon.address_screens.addresses_database

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AddressesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address)

    @Update
    suspend fun update(address: Address)

    @Query("delete from Addresses where addressId = :addressId")
    suspend fun deleteAddress(addressId: Long)

    @Query("select * from Addresses")
    fun getAll(): LiveData<List<Address>>

    @Query("select COUNT(*) from Addresses")
    fun getCount(): LiveData<Int>

    @Query("select * from addresses order by addressId desc limit 1 ")
    suspend fun getLastAddedAddress() : Address

    @Query("select COUNT(*) from addresses")
    fun getAddressesSizeNonLiveData(): Int


}