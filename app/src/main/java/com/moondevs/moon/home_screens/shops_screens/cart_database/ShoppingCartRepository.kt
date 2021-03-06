package com.moondevs.moon.home_screens.shops_screens.cart_database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class ShoppingCartRepository(private val shoppingCartDao: ShoppingCartDao) {

    val toPayAmount: LiveData<Int> = shoppingCartDao.getToPayAmount()
    val alltemsCount : LiveData<Int> = shoppingCartDao.getCount()
    val numberOfUniqueItems : LiveData<Int> = shoppingCartDao.getCarSizeLiveData()
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
        return shoppingCartDao.getRecord(shopItemId)
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

    @WorkerThread
    suspend fun getShopIdFromDB() : String{
        val cartItem =  shoppingCartDao.getShopIdFromDB()
        return cartItem.shopId
    }

    @WorkerThread
    suspend fun getShopRefFromDB() : String{
        val cartItem =  shoppingCartDao.getShopRefFromDB()
        return cartItem.shopRef
    }

    @WorkerThread
    suspend fun getCarSizeNonLiveData(): Int {
        return shoppingCartDao.getCarSizeNonLiveData()
    }



}