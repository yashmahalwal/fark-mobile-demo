package com.fark.mobiledemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fark.mobiledemo.api.graphql.GraphQLClient
import com.fark.mobiledemo.models.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {
    private val graphQLClient = GraphQLClient()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.getOrders().fold(
                onSuccess = { _orders.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
    
    fun createOrder(order: Order) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.createOrder(order).fold(
                onSuccess = { loadOrders() },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
    
    fun updateOrder(order: Order) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.updateOrder(order).fold(
                onSuccess = { loadOrders() },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
    
    fun deleteOrder(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.deleteOrder(id).fold(
                onSuccess = { loadOrders() },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

