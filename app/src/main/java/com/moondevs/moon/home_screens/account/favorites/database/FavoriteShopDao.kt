package com.moondevs.moon.home_screens.account.favorites.database

import androidx.lifecycle.LiveData
import androidx.room.*
@Dao
interface FavoriteShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteShop: FavoriteShop)

    @Query("delete from favoriteshops where shopId = :shopId")
    suspend fun delete(shopId: String)

    @Query("select * from favoriteshops")
    fun getAll(): LiveData<List<FavoriteShop>>


    @Query("select * from favoriteshops where shopId = :shopId")
    suspend fun recordExists(shopId: String): List<FavoriteShop>
}
