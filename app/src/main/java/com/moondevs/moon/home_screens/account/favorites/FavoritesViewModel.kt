package com.moondevs.moon.home_screens.account.favorites

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moondevs.moon.home_screens.account.AccountDatabase
import com.moondevs.moon.home_screens.account.favorites.database.FavoriteShop
import com.moondevs.moon.home_screens.account.favorites.database.FavoriteShopRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoritesViewModel (application: Application): ViewModel(){
    private val repo : FavoriteShopRepository
    val allfavoriteShops : LiveData<List<FavoriteShop>>
    val allfavoriteShopsCount : LiveData<Int>
    init {
        val dao = AccountDatabase.getDatabase(application).favoriteShopDao
        repo = FavoriteShopRepository(dao)
        allfavoriteShops = repo.allfavoriteShops
        allfavoriteShopsCount = repo.allfavoriteShopsCount
    }

    suspend fun insert(favoriteShop : FavoriteShop) = viewModelScope.launch {
        Timber.i("called")
        repo.insert(favoriteShop)
    }

    suspend fun deleteFavoriteShop(shopId: String) = viewModelScope.launch {
        repo.deletefavoriteShop(shopId)
    }

    suspend fun recordExists(shopId : String) : Boolean{
        val favoriteShopsWithThisId = viewModelScope.async{
            repo.recordExists(shopId)
        }
        return favoriteShopsWithThisId.await() >= 1
    }


}