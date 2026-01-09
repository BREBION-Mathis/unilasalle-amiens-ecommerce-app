package com.unilasalle.ecommerce.data.api

import com.unilasalle.ecommerce.data.model.Product

import retrofit2.http.GET
import retrofit2.http.Path


interface IFakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{categoryName}")
    suspend fun getProductsByCategory(@Path("categoryName") category: String): List<Product>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product
}