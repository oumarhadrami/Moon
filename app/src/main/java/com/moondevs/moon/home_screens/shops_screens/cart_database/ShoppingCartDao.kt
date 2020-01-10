package com.moondevs.moon.home_screens.shops_screens.cart_database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.selects.select

@Dao
interface ShoppingCartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItem)

    @Update
    suspend fun update(cartItem: CartItem)

    @Query("delete from ShoppingCart")
    suspend fun emptyCart()

    @Delete
    suspend fun delete(cartItem: CartItem)

    @Query("select * from ShoppingCart")
    fun getAll(): LiveData<List<CartItem>>

    @Query("select IFNULL(SUM(shopItemQuantity),0) from ShoppingCart")
    fun getCount(): LiveData<Int>

    @Query("select * from ShoppingCart where shopItemId = :shopItemId")
    suspend fun recordExists(shopItemId : String) : List<CartItem>

    @Query("select * from ShoppingCart where shopItemId = :shopItemId")
    suspend fun getRecord(shopItemId: String) : CartItem

    @Query("select * from ShoppingCart limit 1")
    suspend fun getShopNameFromDB(): CartItem

    @Query("select * from ShoppingCart limit 1")
    suspend fun getShopImageFromDB(): CartItem

    @Query("select * from ShoppingCart limit 1")
    suspend fun getShopRefFromDB(): CartItem

    @Query("select * from shoppingcart where shopName = :shopName")
    suspend fun dbDoesNotContainThisShop(shopName: String): List<CartItem>

    @Query("select  IFNULL(SUM(shopItemPriceByQuantity),0) from shoppingcart")
    fun getTotalAmount() : LiveData<Int>

    @Query("select 100 + IFNULL(SUM(shopItemPriceByQuantity),0) from shoppingcart")
    fun getToPayAmount(): LiveData<Int>

}