package com.unilasalle.ecommerce.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unilasalle.ecommerce.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "cart_prefs")

class CartRepository(private val context: Context) {
    private val CART_KEY = stringPreferencesKey("cart_json")

    private val gson = Gson()

    val cartItems: Flow<List<CartItem>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[CART_KEY] ?: "[]"
            val type = object : TypeToken<List<CartItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }

    suspend fun saveCart(items: List<CartItem>) {
        val json = gson.toJson(items)
        context.dataStore.edit { preferences ->
            preferences[CART_KEY] = json
        }
    }
}