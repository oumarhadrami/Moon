package com.moondevs.moon.address_screens.addresses_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Address::class], version = 1, exportSchema = false)
abstract class AddressesDatabase : RoomDatabase() {
    abstract val addressesDao: AddressesDao

    companion object{

        @Volatile
        private var INSTANCE : AddressesDatabase? = null
        fun getDatabase(context: Context) : AddressesDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    AddressesDatabase::class.java,
                    "addresses_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}