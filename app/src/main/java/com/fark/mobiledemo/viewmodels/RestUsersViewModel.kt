package com.fark.mobiledemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fark.mobiledemo.api.rest.RestApiClient
import com.fark.mobiledemo.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestUsersViewModel : ViewModel() {
    private val restApiClient = RestApiClient()
    
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.getUsers().fold(
                onSuccess = { _users.value = it },
                onFailure = { _error.value = it.message ?: "Unknown error" }
            )
            _isLoading.value = false
        }
    }
    
    fun createUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.createUser(user).fold(
                onSuccess = { loadUsers() },
                onFailure = { _error.value = it.message ?: "Failed to create user" }
            )
            _isLoading.value = false
        }
    }
    
    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.updateUser(user.id, user).fold(
                onSuccess = { loadUsers() },
                onFailure = { _error.value = it.message ?: "Failed to update user" }
            )
            _isLoading.value = false
        }
    }
    
    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            restApiClient.deleteUser(id).fold(
                onSuccess = { loadUsers() },
                onFailure = { _error.value = it.message ?: "Failed to delete user" }
            )
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}

