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
    
    init {
        loadProducts()
    }
    
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
}

