package com.moondevs.moon.home_screens.shops_list_screen

class Shop{
    var shopId : String? = null
    var shopImage : String? = null
    var shopName: String? = null
    var shopRating: String? = null
    var shopDistance : String? = null

    constructor() {}

    constructor(shopId: String, shopImage: String, shopName: String,
                shopRating: String, shopDistance: String) : this() {
        this.shopId = shopId
        this.shopImage = shopImage
        this.shopName = shopName
        this.shopRating = shopRating
        this.shopDistance = shopDistance
    }

}
