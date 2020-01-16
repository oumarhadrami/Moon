package com.moondevs.moon.home_screens.shops_screens.cart_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moondevs.moon.home_screens.cart.order_database.CurrentOrder
import com.moondevs.moon.home_screens.cart.order_database.CurrentOrdersDao

@Database(entities = [CartItem::class, CurrentOrder::class], version = 1, exportSchema = false)
abstract class ShoppingCartDatabase : RoomDatabase(){
    abstract val shoppingCartDao: ShoppingCartDao
    abstract val currentOrdersDao : CurrentOrdersDao

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