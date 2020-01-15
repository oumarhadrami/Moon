package com.moondevs.moon.home_screens.shops_screens

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moondevs.moon.home_screens.shops_screens.cart_database.CartItem
import com.moondevs.moon.home_screens.shops_screens.cart_database.ShoppingCartDatabase
import com.moondevs.moon.home_screens.shops_screens.cart_database.ShoppingCartRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ShoppingCartViewModel(application: Application): ViewModel()  {
    private val repo : ShoppingCartRepository
    val allCartItems : LiveData<List<CartItem>>
    val allItemsCount : LiveData<Int>
    val totalAmount : LiveData<Int>
    val toPayAmount : LiveData<Int>


    init {
        val dao = ShoppingCartDatabase.getDatabase(application).shoppingCartDao
        repo = ShoppingCartRepository(dao)
        allCartItems =repo.allCartItems
        allItemsCount = repo.alltemsCount
        totalAmount = repo.totalAmout
        toPayAmount = repo.toPayAmount

    }

    suspend fun insert(cartItem: CartItem) = viewModelScope.launch {
        repo.insert(cartItem)
    }

    suspend fun recordExists(shopItemId : String) : Boolean{
        val itemsWithThisId = viewModelScope.async{
            repo.recordExists(shopItemId)
        }
        return itemsWithThisId.await() >= 1
    }

    suspend fun dbDoesNotContainThisShopName(shopName : String) : Boolean{
        val listOfItemsWithThisShopName = viewModelScope.async {
            repo.dbDoesNotContainThisShopName(shopName)
        }
        return listOfItemsWithThisShopName.await() == 0
    }

    suspend fun getRecord(shopItemId: String) : CartItem {
        val cartItemWithThisId = viewModelScope.async {
            repo.getRecord(shopItemId)
        }
        return cartItemWithThisId.await()
    }

    suspend fun emptyCart() = viewModelScope.launch {
        repo.emptyCart()
    }

    suspend fun deleteCartItem(cartItem : CartItem) = viewModelScope.launch {
        repo.deleteCartItem(cartItem)
    }

    suspend fun update(cartItem: CartItem) = viewModelScope.launch {
        repo.update(cartItem)
    }

    fun incrementQuantity(cartItem: CartItem) {
        if (cartItem.shopItemQuantity < 10) {
            cartItem.shopItemQuantity = cartItem.shopItemQuantity.plus(1)
            cartItem.shopItemPriceByQuantity = cartItem.shopItemQuantity * cartItem.shopItemPrice.dropLastWhile { it.isLetter() }.trim().toInt()
        }
        viewModelScope.launch {
            update(cartItem)
        }
    }

    fun decerementQuantity(cartItem: CartItem){
        viewModelScope.launch {
            if (cartItem.shopItemQuantity == 1)
                deleteCartItem(cartItem)
            else {
                cartItem.shopItemQuantity = cartItem.shopItemQuantity.minus(1)
                cartItem.shopItemPriceByQuantity = cartItem.shopItemQuantity * cartItem.shopItemPrice.dropLastWhile { it.isLetter() }.trim().toInt()
                update(cartItem)
            }
        }
    }


    suspend fun getShopNameFromDB() : String {
        val shopName =viewModelScope.async {
            repo.getShopNameFromDB()
        }
        return shopName.await()
    }

    suspend fun getShopImageFromDB() : String {
        val shopImage =viewModelScope.async {
            repo.getShopImageFromDB()
        }
        return shopImage.await()
    }

    suspend fun getShopRefFromDB() : String {
        val shopRef =viewModelScope.async {
            repo.getShopRefFromDB()
        }
        return shopRef.await()
    }

    suspend fun getCarSizeNonLiveData() : Int{
        val totalSize = viewModelScope.async {
            repo.getCarSizeNonLiveData()
        }
        return totalSize.await()
    }


}