package com.moondevs.moon.home_screens.cart.order_database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CurrentOrdersDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currentOrder: CurrentOrder)

    @Update
    suspend fun update(currentOrder: CurrentOrder)

    @Query("delete from currentorders where Id = :currentOrderId")
    suspend fun deleteCurrentOrder(currentOrderId: String)

    @Query("select * from CurrentOrders order by orderDate desc")
    fun getCurrentOrders(): LiveData<List<CurrentOrder>>

    @Query("select * from CurrentOrders order by id desc limit 1 ")
    suspend fun getLastAddedOrder(): CurrentOrder


}