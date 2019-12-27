package com.moondevs.moon.home_screens.shops_screens

class ShopItem {

    var shopItemId : String = ""
    var shopItemImage : String = ""
    var shopItemName: String = ""
    var shopItemPrice: String = ""
    var shopItemCount : Int = 0


    constructor() {}

    constructor(shopItemId: String, shopItemImage: String, shopItemName: String,
                shopItemPrice: String, shopItemCount: Int) : this() {
        this.shopItemId = shopItemId
        this.shopItemImage = shopItemImage
        this.shopItemName = shopItemName
        this.shopItemPrice = shopItemPrice
        this.shopItemCount = shopItemCount
    }
}