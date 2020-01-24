package com.moondevs.moon.home_screens.account.adresses.addresses_database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moondevs.moon.home_screens.account.adresses.addresses_database.Address


@Dao
interface AddressesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: Address) : Long

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
    suspend fun getAddressesSizeNonLiveData(): Int

    @Query("select * from addresses")
    suspend fun getAllAddressesNonLiveData(): List<Address>

    @Query("select * from addresses where addressId = :rowId")
    suspend fun getAddressWithThisId(rowId: Long): Address

    @Query("select * from addresses where isThisTheSelectedAddress = 1")
    suspend fun getSelectedAddress(): Address


}