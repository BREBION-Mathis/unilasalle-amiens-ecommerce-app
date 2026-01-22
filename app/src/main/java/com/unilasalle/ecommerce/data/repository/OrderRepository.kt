package com.unilasalle.ecommerce.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unilasalle.ecommerce.data.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.historyDataStore by preferencesDataStore(name = "history_prefs")

class OrderRepository(private val context: Context) {

    private val HISTORY_KEY = stringPreferencesKey("order_history_json")
    private val gson = Gson()

    val orders: Flow<List<Order>> = context.historyDataStore.data
        .map { preferences ->
            val json = preferences[HISTORY_KEY] ?: "[]"
            val type = object : TypeToken<List<Order>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }

    suspend fun addOrder(order: Order) {
        context.historyDataStore.edit { preferences ->
            val currentJson = preferences[HISTORY_KEY] ?: "[]"
            val type = object : TypeToken<List<Order>>() {}.type
            val currentList: MutableList<Order> = gson.fromJson(currentJson, type) ?: mutableListOf()

            currentList.add(0, order)

            preferences[HISTORY_KEY] = gson.toJson(currentList)
        }
    }
}