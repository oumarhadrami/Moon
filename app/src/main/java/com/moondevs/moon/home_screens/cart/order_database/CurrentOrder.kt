package com.moondevs.moon.home_screens.cart.order_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CurrentOrders")
data class CurrentOrder(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var Id : Long = 0L,

    @ColumnInfo(name = "OrderReference")
    var orderDoc : String
)