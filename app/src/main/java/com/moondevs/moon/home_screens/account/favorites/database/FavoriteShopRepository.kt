package com.moondevs.moon.home_screens.account.favorites.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class FavoriteShopRepository (private val favoriteShopDao: FavoriteShopDao) {

    val allfavoriteShops : LiveData<List<FavoriteShop>> = favoriteShopDao.getAll()

    @WorkerThread
    suspend fun insert(favoriteShop : FavoriteShop){
        favoriteShopDao.insert(favoriteShop)
    }


    @WorkerThread
    suspend fun deletefavoriteShop(shopId: String){
        favoriteShopDao.delete(shopId)
    }

    @WorkerThread
    suspend fun recordExists(shopId : String) : Int{
        val favoriteShopsWithThisId : List<FavoriteShop> = favoriteShopDao.recordExists(shopId)
        return favoriteShopsWithThisId.size
    }
}