package com.fark.mobiledemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fark.mobiledemo.api.rest.RestApiClient
import com.fark.mobiledemo.models.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestOrdersViewModel : ViewModel() {
    private val restApiClient = RestApiClient()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadOrders()
    }
    
    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.getOrders().fold(
                onSuccess = { _orders.value = it },
                onFailure = { _error.value = it.message ?: "Unknown error" }
            )
            _isLoading.value = false
        }
    }
    
    fun createOrder(order: Order) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.createOrder(order).fold(
                onSuccess = { loadOrders() },
                onFailure = { _error.value = it.message ?: "Failed to create order" }
            )
            _isLoading.value = false
        }
    }
}

