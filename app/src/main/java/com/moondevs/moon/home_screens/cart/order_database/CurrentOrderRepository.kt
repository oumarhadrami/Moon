package com.moondevs.moon.home_screens.cart.order_database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class CurrentOrderRepository (private val currentOrderDao: CurrentOrdersDao){
    
    val currentOrders : LiveData<List<CurrentOrder>> = currentOrderDao.getCurrentOrders()

    @WorkerThread
    suspend fun insert(currentOrder : CurrentOrder){
        currentOrderDao.insert(currentOrder)
    }

    @WorkerThread
    suspend fun update(currentOrder : CurrentOrder){
        currentOrderDao.update(currentOrder)
    }

    @WorkerThread
    suspend fun deleteCurrentOrder(currentOrder : CurrentOrder){
        currentOrderDao.deleteCurrentOrder(currentOrder)
    }

    @WorkerThread
    suspend fun getLastAddedOrder(): CurrentOrder {
        return currentOrderDao.getLastAddedOrder()
    }
}