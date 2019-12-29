package com.moondevs.moon.home_screens.shops_screens.cart_database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ShoppingCart")
data class CartItem (


    @ColumnInfo(name = "shopItemId")
    @PrimaryKey
    var shopItemId : String,

    @ColumnInfo(name = "shopItemName")
    var shopItemName : String,

    @ColumnInfo(name = "shopItemPrice")
    var shopItemPrice : String,

    @ColumnInfo(name = "shopItemQuantity")
    var shopItemQuantity : Int,

    @ColumnInfo(name = "shopName")
    var shopName : String)