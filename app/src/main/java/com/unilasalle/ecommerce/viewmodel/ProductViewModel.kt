package com.unilasalle.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unilasalle.ecommerce.data.model.Product
import com.unilasalle.ecommerce.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductUiState {
    data object Loading : ProductUiState
    data class Success(val products: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String>("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private var allProductsCache: List<Product> = emptyList()

    // For sorting
    private var isAscending = true

    init {
        fetchAllProducts()
        fetchCategories()
    }

    fun fetchAllProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val products = repository.getProducts()
                allProductsCache = products // On sauvegarde le cache
                _uiState.value = ProductUiState.Success(products)
                _selectedCategory.value = "All"
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error("Erreur: ${e.message}")
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val cats = repository.getCategories()
                _categories.value = listOf("All") + cats
            } catch (e: Exception) {
                _categories.value = listOf("All")
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _selectedCategory.value = category
            _uiState.value = ProductUiState.Loading
            try {
                // If all products, we use the cache
                // Else, we fetch the products from the API
                val products = if (category == "All") {
                    if (allProductsCache.isNotEmpty()) allProductsCache else repository.getProducts()
                } else {
                    repository.getProductsByCategory(category)
                }

                // If all is set to true, we update the cache
                if (category == "All") allProductsCache = products

                _uiState.value = ProductUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error("Erreur filtre catégorie")
            }
        }
    }

    fun toggleSortOrder() {
        val currentState = _uiState.value
        if (currentState is ProductUiState.Success) {
            isAscending = !isAscending
            val sortedList = if (isAscending) {
                currentState.products.sortedBy { it.price }
            } else {
                currentState.products.sortedByDescending { it.price }
            }
            _uiState.value = ProductUiState.Success(sortedList)
        }
    }

    fun searchProducts(query: String) {
        // If the search bar is empty, we reset to the full cache
        if (query.isBlank()) {
            _selectedCategory.value = "All"
            _uiState.value = ProductUiState.Success(allProductsCache)
            return
        }

        // Else, sort the cache and apply the filter
        val filteredList = allProductsCache.filter { product ->
            product.title.contains(query, ignoreCase = true)
        }
        _uiState.value = ProductUiState.Success(filteredList)
    }
}