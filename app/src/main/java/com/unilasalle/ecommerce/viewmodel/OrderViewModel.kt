package com.unilasalle.ecommerce.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unilasalle.ecommerce.data.model.CartItem
import com.unilasalle.ecommerce.data.model.Order
import com.unilasalle.ecommerce.data.repository.OrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository(application)

    val orders: StateFlow<List<Order>> = repository.orders
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun validateOrder(cartItems: List<CartItem>, totalAmount: Double) {
        viewModelScope.launch {
            val newOrder = Order(
                items = cartItems,
                totalAmount = totalAmount
            )

            repository.addOrder(newOrder)
        }
    }
}