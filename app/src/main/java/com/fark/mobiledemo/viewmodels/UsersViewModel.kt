package com.fark.mobiledemo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fark.mobiledemo.api.graphql.GraphQLClient
import com.fark.mobiledemo.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {
    private val graphQLClient = GraphQLClient()
    
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadUsers()
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.getUsers().fold(
                onSuccess = { _users.value = it },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
    
    fun createUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.createUser(user).fold(
                onSuccess = { loadUsers() },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
    
    fun updateUser(user: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.updateUser(user).fold(
                onSuccess = { loadUsers() },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
    
    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            graphQLClient.deleteUser(id).fold(
                onSuccess = { loadUsers() },
                onFailure = { _error.value = it.message }
            )
            _isLoading.value = false
        }
    }
}

