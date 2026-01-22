package com.unilasalle.ecommerce.data.model

data class CartItem(
    val product: Product,
    var quantity: Int
)