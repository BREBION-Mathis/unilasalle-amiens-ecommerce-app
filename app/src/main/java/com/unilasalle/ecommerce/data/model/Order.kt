package com.unilasalle.ecommerce.data.model

import java.util.Date
import java.util.UUID

data class Order(
    val id: String = UUID.randomUUID().toString(), // Randomly generated
    val date: Long = System.currentTimeMillis(),
    val items: List<CartItem>,
    val totalAmount: Double
)