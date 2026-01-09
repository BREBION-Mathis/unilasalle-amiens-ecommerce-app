package com.unilasalle.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.unilasalle.ecommerce.data.model.Product
import com.unilasalle.ecommerce.data.repository.ProductRepository

// Possible UI states
sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Success(val products: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

class ProductViewModel : ViewModel() {
    // Repository instanciation
    private val repository = ProductRepository()

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String>("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        fetchAllProducts()
        fetchCategories()
    }

    // Action functions called by the UI
    fun fetchAllProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading
            try {
                val products = repository.getProducts()
                _uiState.value = ProductUiState.Success(products)
                _selectedCategory.value = "All"
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error("Erreur lors du chargement: ${e.message}")
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val cats = repository.getCategories()
                _categories.value = listOf("All") + cats
            } catch (e: Exception) {
                // Avoiding an error by keeping the list empty
                _categories.value = listOf("All")
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _selectedCategory.value = category
            _uiState.value = ProductUiState.Loading

            try {
                val products = if (category == "All") {
                    repository.getProducts()
                } else {
                    repository.getProductsByCategory(category)
                }
                _uiState.value = ProductUiState.Success(products)
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error("Impossible de charger la catégorie $category")
            }
        }
    }
}