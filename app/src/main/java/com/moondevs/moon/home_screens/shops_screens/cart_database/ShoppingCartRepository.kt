package com.moondevs.moon.home_screens.shops_screens.cart_database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ShoppingCartRepository(private val shoppingCartDao: ShoppingCartDao) {

    val alltemsCount : LiveData<Int> = shoppingCartDao.getCount()
    val totalAmout : LiveData<Int> = shoppingCartDao.getTotalAmount()
    val allCartItems : LiveData<List<CartItem>> = shoppingCartDao.getAll()

    @WorkerThread
    suspend fun insert(cartItem: CartItem){
        shoppingCartDao.insert(cartItem)
    }

    @WorkerThread
    suspend fun update(cartItem: CartItem){
        shoppingCartDao.update(cartItem)
    }

    @WorkerThread
    suspend fun emptyCart(){
        shoppingCartDao.emptyCart()
    }

    @WorkerThread
    suspend fun deleteCartItem(cartItem: CartItem){
        shoppingCartDao.delete(cartItem)
    }
    @WorkerThread
    suspend fun recordExists(shopItemId : String) : Int{
        val itemsWithThisId : List<CartItem> = shoppingCartDao.recordExists(shopItemId)
        return itemsWithThisId.size
    }

    @WorkerThread
    suspend fun dbDoesNotContainThisShopName(shopName: String): Int {
        val listOfItemWithThisShopName : List<CartItem> = shoppingCartDao.dbDoesNotContainThisShop(shopName)
        return listOfItemWithThisShopName.size
    }

    @WorkerThread
    suspend fun getRecord(shopItemId : String) : CartItem {
        val cartItemWithThisId : CartItem = shoppingCartDao.getRecord(shopItemId)
        return cartItemWithThisId
    }

    @WorkerThread
    suspend fun getAllCartItems() : LiveData<List<CartItem>>{
        return shoppingCartDao.getAll()
    }

    @WorkerThread
    suspend fun getShopNameFromDB() : String{
        val cartItem =  shoppingCartDao.getShopNameFromDB()
        return cartItem.shopName
    }

    @WorkerThread
    suspend fun getShopImageFromDB() : String{
        val cartItem =  shoppingCartDao.getShopImageFromDB()
        return cartItem.shopImage
    }


}