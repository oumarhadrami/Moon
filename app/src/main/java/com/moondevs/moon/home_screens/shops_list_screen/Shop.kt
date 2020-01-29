package com.moondevs.moon.home_screens.shops_list_screen

class Shop{
    var shopId : String? = null
    var shopImage : String? = null
    var shopName: String? = null
    var shopRating: Double? = null
    var numberOfRatings : Double? = null
    var shopLatitude : String? = null
    var shopLongitude : String? = null

    constructor() {}

    constructor(shopId: String, shopImage: String, shopName: String,
                shopRating: Double, numberOfRatings : Double,
                shopLatitude : String, shopLongitude : String) : this() {
        this.shopId = shopId
        this.shopImage = shopImage
        this.shopName = shopName
        this.shopRating = shopRating
        this.numberOfRatings = numberOfRatings
        this.shopLatitude = shopLatitude
        this.shopLongitude = shopLongitude
    }

}
