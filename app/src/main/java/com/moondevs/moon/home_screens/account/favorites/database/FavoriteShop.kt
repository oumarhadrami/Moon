package com.moondevs.moon.home_screens.account.favorites.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoriteshops")
data class FavoriteShop(

    @PrimaryKey
    @ColumnInfo
    var shopId : String,

    @ColumnInfo(name = "shopRef")
    var shopRef : String,

    @ColumnInfo(name = "shopName")
    var shopName : String,

    @ColumnInfo(name = "shopImage")
    var shopImage : String




)