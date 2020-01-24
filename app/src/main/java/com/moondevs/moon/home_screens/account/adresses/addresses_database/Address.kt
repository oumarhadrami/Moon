package com.moondevs.moon.home_screens.account.adresses.addresses_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Addresses")
data class Address(

    @PrimaryKey(autoGenerate = true)
    var addressId : Long = 0L,

    @ColumnInfo(name = "Name")
    var Name : String,

    @ColumnInfo(name = "PhoneNumber")
    var PhoneNumber : String,

    @ColumnInfo(name = "Latitude")
    var Latitude : String,

    @ColumnInfo(name = "Longitude")
    var Longitude : String,

    @ColumnInfo(name = "isThisTheSelectedAddress")
    var isThisTheSelectedAddress : Boolean






)