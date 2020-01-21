package com.moondevs.moon.home_screens.cart.order_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CurrentOrders")
data class CurrentOrder(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var Id : Long = 0L,

    @ColumnInfo(name = "orderReference")
    var orderDoc : String,

    @ColumnInfo(name = "shopName")
    var shopName : String,

    @ColumnInfo(name = "amountToPay")
    var amountToPay : Int,

    @ColumnInfo(name = "totalItemsCount")
    var totalItemsCount : Int,

    @ColumnInfo(name = "orderDate")
    var orderDate : String




)