package com.fark.mobiledemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fark.mobiledemo.api.rest.RestApiClient
import com.fark.mobiledemo.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestProductsViewModel : ViewModel() {
    private val restApiClient = RestApiClient()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.getProducts().fold(
                onSuccess = { _products.value = it },
                onFailure = { _error.value = it.message ?: "Unknown error" }
            )
            _isLoading.value = false
        }
    }
    
    fun createProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.createProduct(product).fold(
                onSuccess = { loadProducts() },
                onFailure = { _error.value = it.message ?: "Failed to create product" }
            )
            _isLoading.value = false
        }
    }
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.updateProduct(product.id, product).fold(
                onSuccess = { loadProducts() },
                onFailure = { _error.value = it.message ?: "Failed to update product" }
            )
            _isLoading.value = false
        }
    }
    
    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.deleteProduct(id).fold(
                onSuccess = { loadProducts() },
                onFailure = { _error.value = it.message ?: "Failed to delete product" }
            )
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

