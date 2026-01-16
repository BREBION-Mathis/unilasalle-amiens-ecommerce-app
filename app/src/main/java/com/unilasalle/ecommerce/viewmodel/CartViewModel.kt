package com.unilasalle.ecommerce.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unilasalle.ecommerce.data.model.CartItem
import com.unilasalle.ecommerce.data.model.Product
import com.unilasalle.ecommerce.data.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CartRepository(application)

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Compute the total price
    val totalPrice: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.product.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    // Compute the amount of articles
    val totalCount: StateFlow<Int> = cartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val currentList = cartItems.value.toMutableList()
            val existingItem = currentList.find { it.product.id == product.id }

            if (existingItem != null) {
                // If the product already exists, we increment its quantity
                val index = currentList.indexOf(existingItem)
                currentList[index] = existingItem.copy(quantity = existingItem.quantity + 1)
            } else {
                // Else we add it to the cart
                currentList.add(CartItem(product, 1))
            }
            repository.saveCart(currentList)
        }
    }

    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            val currentList = cartItems.value.toMutableList()
            currentList.remove(item)
            repository.saveCart(currentList)
        }
    }

    fun updateQuantity(item: CartItem, delta: Int) {
        viewModelScope.launch {
            val currentList = cartItems.value.toMutableList()
            val index = currentList.indexOfFirst { it.product.id == item.product.id }

            if (index != -1) {
                val newQty = item.quantity + delta
                if (newQty > 0) {
                    currentList[index] = item.copy(quantity = newQty)
                } else {
                    currentList.removeAt(index)
                }
                repository.saveCart(currentList)
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.saveCart(emptyList())
        }
    }
}