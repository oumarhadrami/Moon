package com.moondevs.moon.home_screens.account

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moondevs.moon.home_screens.account.adresses.addresses_database.Address
import com.moondevs.moon.home_screens.account.adresses.addresses_database.AddressesDao
import com.moondevs.moon.home_screens.account.favorites.database.FavoriteShop
import com.moondevs.moon.home_screens.account.favorites.database.FavoriteShopDao

@Database(entities = [Address::class, FavoriteShop::class], version = 1, exportSchema = false)
abstract class AccountDatabase : RoomDatabase() {
    abstract val addressesDao: AddressesDao
    abstract val favoriteShopDao : FavoriteShopDao

    companion object{

        @Volatile
        private var INSTANCE : AccountDatabase? = null
        fun getDatabase(context: Context) : AccountDatabase {
            return INSTANCE
                ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    AccountDatabase::class.java,
                    "addresses_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}