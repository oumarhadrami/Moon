package com.moondevs.moon.home_screens.account.orders_list

class CurrentOrderItem {
    var shopName : String? = null
    var orderDate : String? = null
    var amountToPay : Int? = null
    var totalItemsCount : Int? = null
    constructor() {}

    constructor(shopName : String ,orderDate : String,
                amountToPay : Int, totalItemsCount : Int) : this() {

        this.shopName = shopName
        this.orderDate = orderDate
        this.amountToPay = amountToPay
        this.totalItemsCount = totalItemsCount

    }
}