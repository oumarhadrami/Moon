package com.moondevs.moon.home_screens.shops_screens.cart_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItem::class], version = 1, exportSchema = false)
abstract class ShoppingCartDatabase : RoomDatabase(){
    abstract val shoppingCartDao: ShoppingCartDao

    companion object{

        @Volatile
        private var INSTANCE : ShoppingCartDatabase? = null
        fun getDatabase(context: Context) : ShoppingCartDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    ShoppingCartDatabase::class.java,
                    "shopping_cart_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}