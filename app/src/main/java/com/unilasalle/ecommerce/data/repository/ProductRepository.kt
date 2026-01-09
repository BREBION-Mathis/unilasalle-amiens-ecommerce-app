package com.unilasalle.ecommerce.data.repository

import com.unilasalle.ecommerce.data.api.RetrofitInstance
import com.unilasalle.ecommerce.data.model.Product

class ProductRepository {
    private val api = RetrofitInstance.api

    suspend fun getProducts(): List<Product> {
        return api.getProducts()
    }

    suspend fun getCategories(): List<String> {
        return api.getCategories()
    }

    suspend fun getProductsByCategory(category: String): List<Product> {
        return api.getProductsByCategory(category)
    }

    suspend fun getProductById(id: Int): Product {
        return api.getProductById(id)
    }
}