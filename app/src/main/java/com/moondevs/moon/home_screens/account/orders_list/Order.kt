package com.moondevs.moon.home_screens.account.orders_list

class Order {
    var shopName : String? = null
    var orderDate : String? = null
    var isOrderDelivered : Boolean? = null
    var amountToPay : Int? = null
    constructor() {}

    constructor(shopName : String ,orderDate : String,
                isOrderDelivered : Boolean, amountToPay : Int) : this() {

        this.shopName = shopName
        this.orderDate = orderDate
        this.isOrderDelivered = isOrderDelivered
        this.amountToPay = amountToPay
    }
}